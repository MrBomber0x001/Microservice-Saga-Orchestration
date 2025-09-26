package com.saga.core.events;

public class InventoryReserveCommand extends BaseEvent {
    private String productId;
    private int quantity;

    public InventoryReserveCommand() {
    }

    public InventoryReserveCommand(String sagaId, String orderId, String productId, int quantity) {
        super(sagaId, orderId);
        this.productId = productId;
        this.quantity = quantity;
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
}
