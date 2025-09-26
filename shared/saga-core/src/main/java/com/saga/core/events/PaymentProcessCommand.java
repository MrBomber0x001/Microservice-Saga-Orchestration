package com.saga.core.events;

import java.math.BigDecimal;

public class PaymentProcessCommand extends BaseEvent {
    private String customerId;
    private BigDecimal amount;

    public PaymentProcessCommand() {
    }

    public PaymentProcessCommand(String sagaId, String orderId, String customerId, BigDecimal amount) {
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
