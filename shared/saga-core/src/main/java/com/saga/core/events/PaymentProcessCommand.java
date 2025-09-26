package com.saga.core.events;

public class PaymentProcessCommand extends BaseEvent {
    private String customerId;
    private double amount;

    public PaymentProcessCommand() {
    }

    public PaymentProcessCommand(String sagaId, String orderId, String customerId, double amount) {
        super(sagaId, orderId);
        this.customerId = customerId;
        this.amount = amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
