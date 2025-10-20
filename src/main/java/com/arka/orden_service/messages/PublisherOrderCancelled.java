package com.arka.orden_service.messages;

import com.arka.orden_service.config.RabbitMQConfig;
import com.arka.orden_service.dto.OrdenItemDto;
import com.arka.orden_service.dto.OrdenCompleta;
import com.arka.orden_service.model.Orden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PublisherOrderCancelled {

    private final Logger log= LoggerFactory.getLogger(PublisherOrderCancelled.class);
    private  final RabbitTemplate rabbitTemplate;

    public PublisherOrderCancelled(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publisherOrderCancelled(Orden orden){
        try{
            OrdenItemDto items= new OrdenItemDto(
                    orden.getDetalles().stream()
                            .map(detalleOrden -> new OrdenCompleta(
                                    detalleOrden.getIdProducto(),
                                    detalleOrden.getCantidad()
                            )).collect(Collectors.toList())
            );

            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDERS_CANCELLED_EXCHANGE,
                    RabbitMQConfig.ORDERS_CANCELLED_ROUTING_KEY,
                    items);
            log.info("evento enviado correctamente"+items);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
