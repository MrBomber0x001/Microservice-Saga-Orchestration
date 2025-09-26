package com.saga.inventory.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    private String productId;

    @Column(nullable = false)
    private int availableQuantity;

    @Column(nullable = false)
    private int reservedQuantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Inventory() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.availableQuantity = 0;
        this.reservedQuantity = 0;
    }

    public Inventory(String productId, int availableQuantity) {
        this();
        this.productId = productId;
        this.availableQuantity = availableQuantity;
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
        this.updatedAt = LocalDateTime.now();
    }

    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business methods
    public boolean canReserve(int quantity) {
        return availableQuantity >= quantity;
    }

    public void reserve(int quantity) {
        if (canReserve(quantity)) {
            this.availableQuantity -= quantity;
            this.reservedQuantity += quantity;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Insufficient inventory for product: " + productId);
        }
    }

    public void releaseReservation(int quantity) {
        if (reservedQuantity >= quantity) {
            this.reservedQuantity -= quantity;
            this.availableQuantity += quantity;
            this.updatedAt = LocalDateTime.now();
        }
    }
}
