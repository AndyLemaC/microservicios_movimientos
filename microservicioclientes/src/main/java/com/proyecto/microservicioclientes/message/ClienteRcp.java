package com.proyecto.microservicioclientes.message;

import com.proyecto.microservicioclientes.entities.Cliente;
import com.proyecto.microservicioclientes.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteRcp {

    private final ClienteService clienteService;

    @RabbitListener(queues = "${app.rabbitmq.rpc-queue}")
    public Object handleRpcRequest(Map<String, Object> request) {
        log.info("Solicitud RPC recibida: {}", request);

        if (request == null) {
            log.error("Solicitud RPC nula");
            return null;
        }

        Object actionObj = request.get("action");
        if (actionObj == null) {
            log.error("Campo 'action' ausente en la solicitud RPC: {}", request);
            return null;
        }

        String action = String.valueOf(actionObj).trim();

        // Manejo robusto del clienteId: puede venir como Integer, Long o String
        Object idObj = request.get("clienteId");
        Long clienteId = (idObj instanceof Number n) ? n.longValue() : Long.parseLong(idObj.toString());
        if (idObj == null) {
            log.error("Campo 'clienteId' ausente en la solicitud RPC: {}", request);
            return null;
        }

        switch (action) {
            case "VERIFICAR_CLIENTE":
                // retorna Boolean
                return clienteService.existeCliente(clienteId);

            case "OBTENER_NOMBRE_CLIENTE":
                // retorna String (puede ser null si no existe)
                return clienteService.obtenerNombreCliente(clienteId);

            default:
                log.error("Acción no reconocida: {}", action);
                return null;
        }
    }
}