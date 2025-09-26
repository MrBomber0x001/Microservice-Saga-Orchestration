package com.saga.core.events;

import java.math.BigDecimal;

public class PaymentProcessedEvent extends BaseEvent {
    private String paymentId;
    private BigDecimal amount;

    public PaymentProcessedEvent() {
    }

    public PaymentProcessedEvent(String sagaId, String orderId, String paymentId, BigDecimal amount) {
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
