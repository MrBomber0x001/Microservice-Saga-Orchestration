package com.saga.inventory.service;

import com.saga.core.events.BaseEvent;
import com.saga.core.events.CompensateInventoryCommand;
import com.saga.core.events.InventoryReservationFailedEvent;
import com.saga.core.events.InventoryReserveCommand;
import com.saga.core.events.InventoryReservedEvent;
import com.saga.inventory.dto.InitInventoryRequest;
import com.saga.inventory.model.Inventory;
import com.saga.inventory.model.InventoryReservation;
import com.saga.inventory.model.ReservationStatus;
import com.saga.inventory.repository.InventoryRepository;
import com.saga.inventory.repository.InventoryReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryService {


    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryReservationRepository reservationRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Main entry of handling commands and compensation events
     * @param event
     */
    @RabbitListener(queues = "inventory.command.queue")
    @Transactional
    public void handleInventoryCommand(BaseEvent event) {
        logger.info("Received inventory command: {}", event.getClass().getSimpleName());

        switch (event) {
            case InventoryReserveCommand command -> reserveInventory(command);
            case CompensateInventoryCommand command -> compensateInventory(command);
            default -> logger.warn("Unknown inventory command: {}", event.getClass().getSimpleName());
        }
    }

    private void reserveInventory(InventoryReserveCommand command){
        try {
            logger.info("Reserving inventory for product: {}, quantity: {}",
                    command.getProductId(), command.getQuantity());

            Optional<Inventory> inventoryOpt = inventoryRepository.findById(command.getProductId());

            if (inventoryOpt.isEmpty()) {
                sendInventoryReservationFailed(command, "Product not found");
                return;
            }

            Inventory inventory = inventoryOpt.get();

            if (!inventory.canReserve(command.getQuantity())) {
                sendInventoryReservationFailed(command, "Insufficient inventory");
                return;
            }

            // Reserve inventory
            inventory.reserve(command.getQuantity());
            inventoryRepository.save(inventory);

            // Create reservation record
            InventoryReservation reservation = new InventoryReservation();
            reservation.setReservationId(UUID.randomUUID().toString());
            reservation.setOrderId(command.getOrderId());
            reservation.setProductId(command.getProductId());
            reservation.setQuantity(command.getQuantity());
            reservationRepository.save(reservation);

            // Send success event
            InventoryReservedEvent reservedEvent = new InventoryReservedEvent(
                    command.getSagaId(),
                    command.getOrderId(),
                    command.getProductId(),
                    command.getQuantity()
            );

            rabbitTemplate.convertAndSend("saga.exchange", "saga.event.inventory.reserved", reservedEvent);

            logger.info("Inventory reserved successfully for order: {}", command.getOrderId());

        } catch (Exception e) {
            logger.error("Error reserving inventory for order: {}", command.getOrderId(), e);
            sendInventoryReservationFailed(command, "Error reserving inventory: " + e.getMessage());
        }
    }

    private void compensateInventory(CompensateInventoryCommand command) {
        try {
            logger.info("Compensating inventory for order: {}", command.getOrderId());

            Optional<InventoryReservation> reservationOpt = reservationRepository.findByOrderId(command.getOrderId());
            if (reservationOpt.isPresent()) {
                InventoryReservation reservation = reservationOpt.get();
                reservation.setStatus(ReservationStatus.RELEASED);
                reservationRepository.save(reservation);

                // Release inventory
                Optional<Inventory> inventoryOpt = inventoryRepository.findById(reservation.getProductId());
                if (inventoryOpt.isPresent()) {
                    Inventory inventory = inventoryOpt.get();
                    inventory.releaseReservation(reservation.getQuantity());
                    inventoryRepository.save(inventory);

                    logger.info("Inventory compensation completed for order: {}", command.getOrderId());
                }
            } else {
                logger.warn("Reservation not found for compensation, order: {}", command.getOrderId());
            }

        } catch (Exception e) {
            logger.error("Error compensating inventory for order: {}", command.getOrderId(), e);
        }
    }

    private void sendInventoryReservationFailed(InventoryReserveCommand command, String reason) {
        InventoryReservationFailedEvent failedEvent = new InventoryReservationFailedEvent(
                command.getSagaId(),
                command.getOrderId(),
                command.getProductId(),
                command.getQuantity(),
                reason
        );

        rabbitTemplate.convertAndSend("saga.exchange", "saga.event.inventory.reservation.failed", failedEvent);
    }

    public Optional<Inventory> getInventory(String productId) {
        return inventoryRepository.findById(productId);
    }

    @Transactional
    public void initInventory(List<InitInventoryRequest> requests) {
        for (InitInventoryRequest request : requests) {
            Inventory inventory = new Inventory(request.getProductId(), request.getQuantity());
            inventoryRepository.save(inventory);
            logger.info("Initialized inventory for product: {}, quantity: {}",
                    request.getProductId(), request.getQuantity());
        }
    }

}
