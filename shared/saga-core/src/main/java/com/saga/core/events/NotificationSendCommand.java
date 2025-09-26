package com.saga.core.events;

public class NotificationSendCommand extends BaseEvent {
    private String customerId;
    private String message;

    public NotificationSendCommand() {
    }

    public NotificationSendCommand(String sagaId, String orderId, String customerId, String message) {
        super(sagaId, orderId);
        this.customerId = customerId;
        this.message = message;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
