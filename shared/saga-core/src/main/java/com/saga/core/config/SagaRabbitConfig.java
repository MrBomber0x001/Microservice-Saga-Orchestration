package com.saga.core.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaRabbitConfig {

    // Exchanges
    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange("saga.exchange", true, false);
    }

    // Queues
    @Bean
    public Queue inventoryCommandQueue() {
        return QueueBuilder.durable("inventory.command.queue").build();
    }

    @Bean
    public Queue inventoryEventQueue() {
        return QueueBuilder.durable("inventory.event.queue").build();
    }

    @Bean
    public Queue paymentCommandQueue() {
        return QueueBuilder.durable("payment.command.queue").build();
    }

    @Bean
    public Queue paymentEventQueue() {
        return QueueBuilder.durable("payment.event.queue").build();
    }

    @Bean
    public Queue notificationCommandQueue() {
        return QueueBuilder.durable("notification.command.queue").build();
    }

    @Bean
    public Queue notificationEventQueue() {
        return QueueBuilder.durable("notification.event.queue").build();
    }

    @Bean
    public Queue sagaOrchestratorQueue() {
        return QueueBuilder.durable("saga.orchestrator.queue").build();
    }

    // Bindings
    @Bean
    public Binding inventoryCommandBinding() {
        return BindingBuilder.bind(inventoryCommandQueue())
                .to(sagaExchange())
                .with("inventory.command.*");
    }

    @Bean
    public Binding inventoryEventBinding() {
        return BindingBuilder.bind(inventoryEventQueue())
                .to(sagaExchange())
                .with("inventory.event.*");
    }

    @Bean
    public Binding paymentCommandBinding() {
        return BindingBuilder.bind(paymentCommandQueue())
                .to(sagaExchange())
                .with("payment.command.*");
    }

    @Bean
    public Binding paymentEventBinding() {
        return BindingBuilder.bind(paymentEventQueue())
                .to(sagaExchange())
                .with("payment.event.*");
    }

    @Bean
    public Binding notificationCommandBinding() {
        return BindingBuilder.bind(notificationCommandQueue())
                .to(sagaExchange())
                .with("notification.command.*");
    }

    @Bean
    public Binding notificationEventBinding() {
        return BindingBuilder.bind(notificationEventQueue())
                .to(sagaExchange())
                .with("notification.event.*");
    }

    @Bean
    public Binding sagaOrchestratorBinding() {
        return BindingBuilder.bind(sagaOrchestratorQueue())
                .to(sagaExchange())
                .with("saga.event.#");
    }

    // Message converter
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    // Listener container factory
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}
