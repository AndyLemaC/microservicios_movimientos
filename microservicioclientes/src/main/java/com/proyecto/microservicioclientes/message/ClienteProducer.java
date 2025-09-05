package com.proyecto.microservicioclientes.message;

import com.proyecto.microservicioclientes.entities.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    public void sendClienteCreatedMessage(Cliente cliente) {
        MessageEvent<Cliente> message = new MessageEvent<>("CLIENTE_CREATED", cliente);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    public void sendClienteUpdatedMessage(Cliente cliente) {
        MessageEvent<Cliente> message = new MessageEvent<>("CLIENTE_UPDATED", cliente);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    public void sendClienteDeletedMessage(Long clienteId) {
        MessageEvent<Long> message = new MessageEvent<>("CLIENTE_DELETED", clienteId);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
