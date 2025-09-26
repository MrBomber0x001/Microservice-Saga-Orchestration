package com.saga.core.model;

public enum SagaState {
    STARTED,
    INVENTORY_RESERVED,
    PAYMENT_PROCESSED,
    COMPLETED,
    COMPENSATING,
    FAILED
}

