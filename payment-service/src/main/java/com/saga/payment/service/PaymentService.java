package com.saga.payment.service;

import com.saga.core.events.*;
import com.saga.payment.model.Payment;
import com.saga.payment.model.PaymentStatus;
import com.saga.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "payment.command.queue")
    @Transactional
    public void handlePaymentCommand(BaseEvent event) {
        logger.info("Received payment command: {}", event.getClass().getSimpleName());

        switch (event) {
            case PaymentProcessCommand command -> processPayment(command);
            case CompensatePaymentCommand command -> compensatePayment(command);
            default -> logger.warn("Unknown payment command: {}", event.getClass().getSimpleName());
        }
    }

    private void processPayment(PaymentProcessCommand command) {
        try {
            logger.info("Processing payment for order: {}", command.getOrderId());

            // Simulate payment processing logic
            if (command.getAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
                // Simulate payment failure for negative amounts
                PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                        command.getSagaId(),
                        command.getOrderId(),
                        command.getAmount(),
                        "Invalid payment amount"
                );

                rabbitTemplate.convertAndSend("saga.exchange", "saga.event.payment.failed", failedEvent);
                return;
            }

            // Create payment record
            Payment payment = new Payment();
            payment.setPaymentId(UUID.randomUUID().toString());
            payment.setOrderId(command.getOrderId());
            payment.setCustomerId(command.getCustomerId());
            payment.setAmount(command.getAmount());
            payment.setStatus(PaymentStatus.PROCESSED);

            paymentRepository.save(payment);

            // Send success event
            PaymentProcessedEvent processedEvent = new PaymentProcessedEvent(
                    command.getSagaId(),
                    command.getOrderId(),
                    payment.getPaymentId(),
                    command.getAmount()
            );

            rabbitTemplate.convertAndSend("saga.exchange", "saga.event.payment.processed", processedEvent);

            logger.info("Payment processed successfully for order: {}", command.getOrderId());

        } catch (Exception e) {
            logger.error("Error processing payment for order: {}", command.getOrderId(), e);

            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                    command.getSagaId(),
                    command.getOrderId(),
                    command.getAmount(),
                    "Payment processing error: " + e.getMessage()
            );

            rabbitTemplate.convertAndSend("saga.exchange", "saga.event.payment.failed", failedEvent);
        }
    }

    private void compensatePayment(CompensatePaymentCommand command) {
        try {
            logger.info("Compensating payment for order: {}", command.getOrderId());

            Optional<Payment> paymentOpt = paymentRepository.findByOrderId(command.getOrderId());
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);

                logger.info("Payment compensation completed for order: {}", command.getOrderId());
            } else {
                logger.warn("Payment not found for compensation, order: {}", command.getOrderId());
            }

        } catch (Exception e) {
            logger.error("Error compensating payment for order: {}", command.getOrderId(), e);
        }
    }

    public Optional<Payment> getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    public Optional<Payment> getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}

