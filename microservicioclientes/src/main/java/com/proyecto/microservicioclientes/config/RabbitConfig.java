package com.proyecto.microservicioclientes.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit

public class RabbitConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.queue}")
    private String clienteQueue;

    @Value("${app.rabbitmq.routing-key}")
    private String clienteRoutingKey;

    @Value("${app.rabbitmq.rpc-queue}")
    private String clienteRpcQueue;

    @Value("${app.rabbitmq.rpc-routing-key:cliente.rpc.key}")
    private String clienteRpcRoutingKey;

    @Bean
    public DirectExchange exchange() { return new DirectExchange(exchange); }

    @Bean
    public Queue clienteQueue() {
        return new Queue(clienteQueue, true);
    }

    @Bean
    public Queue clienteRpcQueue() {
        return new Queue(clienteRpcQueue, true);
    }

    @Bean
    public Binding clienteBinding(Queue clienteQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(clienteQueue)
                .to(exchange)
                .with(clienteRoutingKey);
    }


    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

    @Bean
    public Binding clienteRpcBinding(Queue clienteRpcQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(clienteRpcQueue)
                .to(exchange)
                .with("cliente.rpc.key"); // Routing key específica para RPC
    }

}
