package com.proyecto.microservicioclientes.message;

import com.proyecto.microservicioclientes.entities.Cliente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClienteEventListener {

    /**
     * Este listener consume mensajes que llegan a la cola cliente-cuenta-queue
     */
    @RabbitListener(queues = "${app.rabbitmq.queue}") // -> app.rabbitmq.queue=cliente-cuenta-queue
    public void onClienteEvento(Cliente cliente) {
        log.info("📥 Evento de cliente recibido desde RabbitMQ: {}", cliente);

        // Aquí decides qué hacer:
        // - Guardar en la BD
        // - Actualizar cache
        // - Solo imprimir (si no necesitas persistencia)
    }
}
