package com.saga.core.events;

public class PaymentProcessedEvent extends BaseEvent {
    private String paymentId;
    private double amount;

    public PaymentProcessedEvent() {
    }

    public PaymentProcessedEvent(String sagaId, String orderId, String paymentId, double amount) {
        super(sagaId, orderId);
        this.paymentId = paymentId;
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
