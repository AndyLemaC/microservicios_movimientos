package com.proyecto.microserviciomovimientos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.queue.cliente}")
    private String clienteQueue;

    @Value("${rabbitmq.routing.key.cliente}")
    private String clienteRoutingKey;

    @Value("${rabbitmq.queue.cuenta}")
    private String cuentaQueue;

    @Value("${rabbitmq.routing.key.cuenta}")
    private String cuentaRoutingKey;

    // Bean para el exchange
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    // Bean para la cola de clientes
    @Bean
    public Queue clienteQueue() {
        return new Queue(clienteQueue, true);
    }

    // Bean para la cola de cuentas
    @Bean
    public Queue cuentaQueue() {
        return new Queue(cuentaQueue, true);
    }

    // Binding para la cola de clientes
    @Bean
    public Binding clienteBinding() {
        return BindingBuilder
                .bind(clienteQueue())
                .to(exchange())
                .with(clienteRoutingKey);
    }

    // Binding para la cola de cuentas
    @Bean
    public Binding cuentaBinding() {
        return BindingBuilder
                .bind(cuentaQueue())
                .to(exchange())
                .with(cuentaRoutingKey);
    }

    // Convertidor de mensajes JSON
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Template para RabbitMQ
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
