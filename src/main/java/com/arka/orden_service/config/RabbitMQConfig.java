package com.arka.orden_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    //listener
    public static final String ORDERS_CONFIRMED_EXCHANGE= "confirmed.exchange";
    public static  final String ORDERS_CONFIRMED_ROUTING_KEY="order.confirmed";
    public static final String ORDER_CONFIRMED_QUEUE = "order.confirmed.queue";

    //publisher
    public static final String ORDERS_CANCELLED_EXCHANGE="cancelled.exchange";
    public static final String ORDERS_CANCELLED_ROUTING_KEY="cancelled.order";

    /// RETRY FOR ERRORS
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_INITIAL_INTERVAL = 2000L;
    private static final long RETRY_MAX_INTERVAL = 10000L;
    private static final double RETRY_MULTIPLIER = 2.0;

    @Bean
    public TopicExchange orderConfirmedExchange() {
        return new TopicExchange(ORDERS_CONFIRMED_EXCHANGE);
    }

    @Bean
    public TopicExchange orderCancelledExchange(){
        return new TopicExchange(ORDERS_CANCELLED_EXCHANGE);
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return new Queue(ORDER_CONFIRMED_QUEUE, true);
    }

    @Bean
    public Binding orderConfirmedBinding() {
        return BindingBuilder
                .bind(orderConfirmedQueue())
                .to(orderConfirmedExchange())
                .with(ORDERS_CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public org.springframework.retry.support.RetryTemplate retryTemplate() {
        org.springframework.retry.support.RetryTemplate retryTemplate =
                new org.springframework.retry.support.RetryTemplate();
        org.springframework.retry.backoff.ExponentialBackOffPolicy backOffPolicy =
                new org.springframework.retry.backoff.ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(RETRY_INITIAL_INTERVAL);
        backOffPolicy.setMaxInterval(RETRY_MAX_INTERVAL);
        backOffPolicy.setMultiplier(RETRY_MULTIPLIER);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        org.springframework.retry.policy.SimpleRetryPolicy retryPolicy =
                new org.springframework.retry.policy.SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(MAX_RETRY_ATTEMPTS);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

}
