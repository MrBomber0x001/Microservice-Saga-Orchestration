package com.saga.order.service;

import com.saga.core.events.BaseEvent;
import com.saga.core.events.CompensateInventoryCommand;
import com.saga.core.events.InventoryReservationFailedEvent;
import com.saga.core.events.InventoryReserveCommand;
import com.saga.core.events.InventoryReservedEvent;
import com.saga.core.events.NotificationSendCommand;
import com.saga.core.events.NotificationSentEvent;
import com.saga.core.events.PaymentFailedEvent;
import com.saga.core.events.PaymentProcessCommand;
import com.saga.core.events.PaymentProcessedEvent;
import com.saga.core.model.SagaState;
import com.saga.core.model.SagaStep;
import com.saga.core.model.SagaTransaction;
import com.saga.order.model.Order;
import com.saga.order.model.OrderStatus;
import com.saga.order.repository.SagaTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SagaOrchestratorService {
    private static final Logger logger = LoggerFactory.getLogger(SagaOrchestratorService.class);

    @Autowired
    private SagaTransactionRepository sagaRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderService orderService;

    /**
     * The entry point
     * @param order
     */
    public void startOrderSaga(Order order){
        SagaTransaction saga = new SagaTransaction(order.getOrderId());
        saga.setCurrentStep(SagaStep.RESERVE_INVENTORY);
        sagaRepository.save(saga);

        logger.info("Started saga {} for order {}", saga.getSagaId(), order.getOrderId());

        // send inventory reservation command
        InventoryReserveCommand command = new InventoryReserveCommand(
                saga.getSagaId(),
                order.getOrderId(),
                order.getProductId(),
                order.getQuantity()
        );

        rabbitTemplate.convertAndSend("saga.exchange", "inventory.command.reserve", command);
    }

    /**
     * the main entry of orchestration
     * @param event
     */
    @RabbitListener(queues = "saga.orchestrator.queue")
    @Transactional
    public void handleSagaEvent(BaseEvent event){
        logger.info("Received event: {} for saga: {}", event.getClass().getSimpleName(), event.getSagaId());

        Optional<SagaTransaction> sagaOpt = sagaRepository.findById(event.getSagaId());
        if (sagaOpt.isEmpty()) {
            logger.error("Saga not found: {}", event.getSagaId());
            return;
        }

        SagaTransaction saga = sagaOpt.get();
        try {
            switch (event){
                case InventoryReservedEvent inventoryEvent -> handleInventoryReserved(saga, inventoryEvent);
                case InventoryReservationFailedEvent inventoryFailedEvent -> handleInventoryFailed(saga, inventoryFailedEvent);
                case PaymentProcessedEvent paymentEvent -> handlePaymentProcessed(saga, paymentEvent);
                case PaymentFailedEvent paymentFailedEvent -> handlePaymentFailed(saga, paymentFailedEvent);
                case NotificationSentEvent notificationEvent -> handleNotificationSent(saga, notificationEvent);
                default -> logger.warn("Unknown event type: {}", event.getClass().getSimpleName());
            }
        } catch (Exception e){
            logger.error("Error processing saga event", e);
            saga.setState(SagaState.FAILED);
            saga.setErrorMessage(e.getMessage());
            sagaRepository.save(saga);
        }

    }

    private void handleInventoryReserved(SagaTransaction saga, InventoryReservedEvent event){
        saga.setState(SagaState.INVENTORY_RESERVED);
        saga.setCurrentStep(SagaStep.PROCESS_PAYMENT);
        sagaRepository.save(saga);

        // Get order details for payment
        orderService.getOrder(saga.getOrderId()).ifPresent(order -> {
            //initial payment processing
            PaymentProcessCommand command = new PaymentProcessCommand(
                    saga.getSagaId(),
                    saga.getOrderId(),
                    order.getCustomerId(),
                    order.getAmount()
            );

            rabbitTemplate.convertAndSend("saga.exchange", "payment.command.process", command);
        });
    }

    private void handleInventoryFailed(SagaTransaction saga, InventoryReservationFailedEvent event) {
        saga.setState(SagaState.FAILED);
        saga.setErrorMessage("Inventory reservation failed: " + event.getReason());
        sagaRepository.save(saga);

        orderService.updateOrderStatus(saga.getOrderId(), OrderStatus.CANCELLED);
        logger.info("Saga {} failed due to inventory reservation failure", saga.getSagaId());
    }

    private void handlePaymentProcessed(SagaTransaction saga, PaymentProcessedEvent event) {
        saga.setState(SagaState.PAYMENT_PROCESSED);
        saga.setCurrentStep(SagaStep.SEND_NOTIFICATION);
        sagaRepository.save(saga);

        // Get order details for notification
        orderService.getOrder(saga.getOrderId()).ifPresent(order -> {
            NotificationSendCommand command = new NotificationSendCommand(
                    saga.getSagaId(),
                    saga.getOrderId(),
                    order.getCustomerId(),
                    "Your order " + order.getOrderId() + " has been confirmed!"
            );

            rabbitTemplate.convertAndSend("saga.exchange", "notification.command.send", command);
        });
    }

    // **** Compensation ****** //
    public void handlePaymentFailed(SagaTransaction saga, PaymentFailedEvent event){
        saga.setState(SagaState.COMPENSATING);
        saga.setErrorMessage("Payment failed: " + event.getReason());
        sagaRepository.save(saga);

        orderService.getOrder(saga.getOrderId()).ifPresent(order -> {
            CompensateInventoryCommand command = new CompensateInventoryCommand(
                    saga.getSagaId(),
                    saga.getOrderId(),
                    order.getProductId(),
                    order.getQuantity()
            );

            rabbitTemplate.convertAndSend("saga.exchange", "inventory.command.compensate", command);
        });
    }

    private void handleNotificationSent(SagaTransaction saga, NotificationSentEvent event) {
        saga.setState(SagaState.COMPLETED);
        sagaRepository.save(saga);

        orderService.updateOrderStatus(saga.getOrderId(), OrderStatus.CONFIRMED);
        logger.info("Saga {} completed successfully", saga.getSagaId());
    }

    public Optional<SagaTransaction> getSaga(String sagaId) {
        return sagaRepository.findById(sagaId);
    }

    public Optional<SagaTransaction> getSagaByOrderId(String orderId) {
        return sagaRepository.findByOrderId(orderId);
    }

}
