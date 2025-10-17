package com.arka.orden_service.service;

import com.arka.orden_service.dto.CrearOrdenEventDto;
import com.arka.orden_service.model.DetalleOrden;
import com.arka.orden_service.model.Estado;
import com.arka.orden_service.model.Orden;
import com.arka.orden_service.repository.OrdenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenService {

    private final OrdenRepository repository;
    private final UsuarioDireccionService usuarioDireccionService;

    public OrdenService(OrdenRepository repository, UsuarioDireccionService usuarioDireccionService) {
        this.repository = repository;
        this.usuarioDireccionService = usuarioDireccionService;
    }

    @Transactional
    public Mono<Orden> crearOrden(CrearOrdenEventDto dto) {


        return usuarioDireccionService.obtenerDireccionDeUsuario(dto.getIdUsuario())
                .flatMap(direccion -> Mono.fromCallable(() -> {

                    cancelarOrdenPendiente(dto.getIdUsuario());

                    Orden orden = new Orden(
                            dto.getIdUsuario(),
                            dto.getIdCarrito(),
                            dto.getFechaCreacion(),
                            dto.getMontoTotal()
                    );

                    orden.setDireccion(direccion.getDireccion());
                    orden.setCiudad(direccion.getCiudad());
                    orden.setPais(direccion.getPais());

                    dto.getDetalles().forEach(detalleDto -> {
                        DetalleOrden detalle = new DetalleOrden(
                                detalleDto.getIdProducto(),
                                detalleDto.getCantidad(),
                                detalleDto.getPrecioUnitario(),
                                detalleDto.getSubtotal()
                        );
                        orden.addDetalle(detalle);
                    });

                    return repository.save(orden);

                }).subscribeOn(Schedulers.boundedElastic()))
                .switchIfEmpty(Mono.fromCallable(() -> {
                    cancelarOrdenPendiente(dto.getIdUsuario());

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
                        orden.addDetalle(detalle);
                    });

                    return repository.save(orden);
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    private void cancelarOrdenPendiente(Integer idUsuario) {
        repository.findByIdUsuarioAndEstado(idUsuario, Estado.pendiente)
                .ifPresent(orden -> {
                    orden.setEstado(Estado.cancelada);
                    repository.save(orden);
                });
    }

}
