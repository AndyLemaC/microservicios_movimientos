package com.proyecto.microserviciomovimientos.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key.rpc:cliente.rpc.key}")
    private String rpcRoutingKey;


    public String obtenerNombreCliente(Long clienteId) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("action", "OBTENER_NOMBRE_CLIENTE");
            message.put("clienteId", clienteId);

            Object respuesta = rabbitTemplate.convertSendAndReceive(exchange, rpcRoutingKey, message);

            if (respuesta instanceof String nombre && !nombre.isBlank()) {
                return nombre;
            } else {
                log.warn("RPC sin nombre para clienteId={}. Respuesta: {}", clienteId, respuesta);
                return "Cliente " + clienteId; // fallback
            }
        } catch (Exception e) {
            log.error("Error al obtener nombre del cliente via RPC (id={}): {}", clienteId, e.getMessage(), e);
            return "Cliente " + clienteId; // fallback
        }
    }

    public boolean verificarCliente(Long clienteId) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("action", "VERIFICAR_CLIENTE");
            message.put("clienteId", clienteId);

            Object respuesta = rabbitTemplate.convertSendAndReceive(exchange, rpcRoutingKey, message);
            return respuesta instanceof Boolean b && b;
        } catch (Exception e) {
            log.error("Error verificando existencia de cliente via RPC (id={}): {}", clienteId, e.getMessage(), e);
            return false;
        }
    }
}
