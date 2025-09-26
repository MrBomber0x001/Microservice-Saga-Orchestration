package com.saga.core.events;

public class NotificationSentEvent extends BaseEvent {
    private String notificationId;
    private String customerId;

    public NotificationSentEvent() {
    }

    public NotificationSentEvent(String sagaId, String orderId, String notificationId, String customerId) {
        super(sagaId, orderId);
        this.notificationId = notificationId;
        this.customerId = customerId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
