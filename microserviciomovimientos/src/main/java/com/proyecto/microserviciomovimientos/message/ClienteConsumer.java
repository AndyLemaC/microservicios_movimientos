package com.proyecto.microserviciomovimientos.message;

import com.proyecto.microserviciomovimientos.service.CuentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteConsumer {

    private final CuentaService cuentaService;

    @RabbitListener(queues = "${rabbitmq.queue.cuenta}")
    public Map<String, Object> processMessage(Map<String, Object> message) {
        log.info("Mensaje recibido: {}", message);

        String action = (String) message.get("action");
        Map<String, Object> response = new HashMap<>();

        switch (action) {
            case "VERIFICAR_CLIENTE_TIENE_CUENTAS":
                Long clienteId = Long.valueOf(message.get("clienteId").toString());
                boolean tieneCuentas = cuentaService.existsByClienteId(clienteId);

                response.put("action", "RESPUESTA_VERIFICAR_CLIENTE_TIENE_CUENTAS");
                response.put("clienteId", clienteId);
                response.put("tieneCuentas", tieneCuentas);
                break;

            default:
                log.warn("Acción desconocida: {}", action);
                response.put("error", "Acción desconocida");
                break;
        }

        return response;
    }
}
