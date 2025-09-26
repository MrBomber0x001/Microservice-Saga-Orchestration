package com.saga.order.service;

import com.saga.order.dto.OrderRequest;
import com.saga.order.model.Order;
import com.saga.order.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;


    private final RabbitTemplate rabbitTemplate;

    private final SagaOrchestratorService sagaOrchestratorService;

    public OrderService(
            OrderRepository orderRepository,
            RabbitTemplate rabbitTemplate,
            @Lazy SagaOrchestratorService sagaOrchestratorService
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderRepository = orderRepository;
        this.sagaOrchestratorService = sagaOrchestratorService;
    }

    public Order createOrder(OrderRequest request){
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setAmount(request.getAmount());

        order = orderRepository.save(order);
        sagaOrchestratorService.startOrderSaga(order);

        return order;
    }

    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }

    public void updateOrderStatus(String orderId, com.saga.order.model.OrderStatus status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }
}
