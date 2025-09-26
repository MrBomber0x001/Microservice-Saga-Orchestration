package com.saga.core.events;

public class PaymentFailedEvent extends BaseEvent {
    private double amount;
    private String reason;

    public PaymentFailedEvent() {
    }

    public PaymentFailedEvent(String sagaId, String orderId, double amount, String reason) {
        super(sagaId, orderId);
        this.amount = amount;
        this.reason = reason;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
