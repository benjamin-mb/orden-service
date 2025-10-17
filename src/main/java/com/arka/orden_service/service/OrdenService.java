package com.arka.orden_service.service;

import com.arka.orden_service.dto.CrearOrdenEventDto;
import com.arka.orden_service.model.DetalleOrden;
import com.arka.orden_service.model.Orden;
import com.arka.orden_service.repository.OrdenRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrdenService {

    private final OrdenRepository repository;

    public OrdenService(OrdenRepository repository) {
        this.repository = repository;
    }

    public Mono<Orden>crearOrden(CrearOrdenEventDto dto){
        return Mono.fromCallable(()->{

            List<DetalleOrden> detalles=new ArrayList<>();
            Orden orden = new Orden(
                    dto.getIdUsuario(),
                    dto.getIdCarrito(),
                    dto.getFechaCreacion(),
                    dto.getMontoTotal()
            );

            dto.getDetalles().forEach(detalleDto -> {
                DetalleOrden detalle = new DetalleOrden(
                        detalleDto.getIdProducto(),
                        detalleDto.getCantidad(),
                        detalleDto.getPrecioUnitario(),
                        detalleDto.getSubtotal()
                );
                detalles.add(detalle);
            });

            orden.setDetalles(detalles);
            return repository.save(orden);
        }).subscribeOn(Schedulers.boundedElastic());

    }
}
