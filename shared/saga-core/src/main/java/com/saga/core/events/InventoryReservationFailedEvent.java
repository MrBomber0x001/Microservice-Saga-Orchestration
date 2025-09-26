package com.saga.core.events;

public class InventoryReservationFailedEvent extends BaseEvent {
    private String productId;
    private int quantity;
    private String reason;

    public InventoryReservationFailedEvent() {
    }

    public InventoryReservationFailedEvent(String sagaId, String orderId, String productId, int quantity, String reason) {
        super(sagaId, orderId);
        this.productId = productId;
        this.quantity = quantity;
        this.reason = reason;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
