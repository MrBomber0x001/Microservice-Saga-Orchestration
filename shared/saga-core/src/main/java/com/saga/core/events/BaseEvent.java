package com.saga.core.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderCreatedEvent.class, name = "ORDER_CREATED"),
        @JsonSubTypes.Type(value = InventoryReserveCommand.class, name = "INVENTORY_RESERVE_COMMAND"),
        @JsonSubTypes.Type(value = InventoryReservedEvent.class, name = "INVENTORY_RESERVED"),
        @JsonSubTypes.Type(value = InventoryReservationFailedEvent.class, name = "INVENTORY_RESERVATION_FAILED"),
        @JsonSubTypes.Type(value = PaymentProcessCommand.class, name = "PAYMENT_PROCESS_COMMAND"),
        @JsonSubTypes.Type(value = PaymentProcessedEvent.class, name = "PAYMENT_PROCESSED"),
        @JsonSubTypes.Type(value = PaymentFailedEvent.class, name = "PAYMENT_FAILED"),
        @JsonSubTypes.Type(value = NotificationSendCommand.class, name = "NOTIFICATION_SEND_COMMAND"),
        @JsonSubTypes.Type(value = NotificationSentEvent.class, name = "NOTIFICATION_SENT"),
        @JsonSubTypes.Type(value = CompensateInventoryCommand.class, name = "COMPENSATE_INVENTORY_COMMAND"),
        @JsonSubTypes.Type(value = CompensatePaymentCommand.class, name = "COMPENSATE_PAYMENT_COMMAND")
})
public abstract class BaseEvent {
    private String eventId;
    private String sagaId;
    private String orderId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public BaseEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public BaseEvent(String sagaId, String orderId) {
        this();
        this.sagaId = sagaId;
        this.orderId = orderId;
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getSagaId() { return sagaId; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}


