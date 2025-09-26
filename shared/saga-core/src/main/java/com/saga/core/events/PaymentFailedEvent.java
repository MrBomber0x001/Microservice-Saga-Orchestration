package com.saga.core.events;

import java.math.BigDecimal;

public class PaymentFailedEvent extends BaseEvent {
    private BigDecimal amount;
    private String reason;

    public PaymentFailedEvent() {
    }

    public PaymentFailedEvent(String sagaId, String orderId, BigDecimal amount, String reason) {
        super(sagaId, orderId);
        this.amount = amount;
        this.reason = reason;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
