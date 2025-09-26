package com.saga.core.events;

public class OrderCreatedEvent extends BaseEvent {
    private String customerId;
    private String productId;
    private int quantity;
    private double amount;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(String sagaId, String orderId, String customerId, String productId, int quantity, double amount) {
        super(sagaId, orderId);
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
    }

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
