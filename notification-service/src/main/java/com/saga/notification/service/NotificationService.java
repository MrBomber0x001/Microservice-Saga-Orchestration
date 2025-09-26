package com.saga.notification.service;

import com.saga.core.events.BaseEvent;
import com.saga.core.events.NotificationSendCommand;
import com.saga.core.events.NotificationSentEvent;
import com.saga.notification.model.Notification;
import com.saga.notification.model.NotificationStatus;
import com.saga.notification.repository.NotificationRepository;
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
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "notification.command.queue")
    @Transactional
    public void handleNotificationCommand(BaseEvent event) {
        logger.info("Received notification command: {}", event.getClass().getSimpleName());

        switch (event) {
            case NotificationSendCommand command -> sendNotification(command);
            default -> logger.warn("Unknown notification command: {}", event.getClass().getSimpleName());
        }
    }

    private void sendNotification(NotificationSendCommand command) {
        try {
            logger.info("Sending notification for order: {}", command.getOrderId());

            // Create notification record
            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setOrderId(command.getOrderId());
            notification.setCustomerId(command.getCustomerId());
            notification.setMessage(command.getMessage());
            notification.setStatus(NotificationStatus.SENT);

            notificationRepository.save(notification);

            // Simulate notification sending (email, SMS, push notification, etc.)
            logger.info("📧 Sending notification to customer {}: {}",
                    command.getCustomerId(), command.getMessage());

            // Send success event
            NotificationSentEvent sentEvent = new NotificationSentEvent(
                    command.getSagaId(),
                    command.getOrderId(),
                    notification.getNotificationId(),
                    command.getCustomerId()
            );

            rabbitTemplate.convertAndSend("saga.exchange", "saga.event.notification.sent", sentEvent);

            logger.info("Notification sent successfully for order: {}", command.getOrderId());

        } catch (Exception e) {
            logger.error("Error sending notification for order: {}", command.getOrderId(), e);
        }
    }

    public Optional<Notification> getNotification(String notificationId) {
        return notificationRepository.findById(notificationId);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getCustomerNotifications(String customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }
}