package com.arka.orden_service.messages;

import com.arka.orden_service.config.RabbitMQConfig;
import com.arka.orden_service.dto.CrearOrdenEventDto;
import com.arka.orden_service.model.Orden;
import com.arka.orden_service.service.OrdenService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Listener {

    private static final Logger log= LoggerFactory.getLogger(Listener.class);
    private final OrdenService service;


    public Listener(OrdenService service) {
        this.service = service;
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_CONFIRMED_QUEUE)
    public void manejadorParaCrearOrden(CrearOrdenEventDto event, Channel channel, Message message){
        log.info("🔔 Evento recibido | Usuario: {} | Carrito: {} | Monto: {}",
                event.getIdUsuario(), event.getIdCarrito(), event.getMontoTotal());

        service.crearOrden(event)
                .doOnSuccess(orden -> log.info("✅ Orden creada con ID: {}", orden.getId()))
                .doOnError(error -> log.error("❌ Error creando orden: {}", error.getMessage()))
                .subscribe();
    }

}
