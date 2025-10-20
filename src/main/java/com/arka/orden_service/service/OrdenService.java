package com.arka.orden_service.service;

import com.arka.orden_service.component.NotificationsComponent;
import com.arka.orden_service.dto.CrearOrdenEventDto;
import com.arka.orden_service.dto.DetalleOrdenDto;
import com.arka.orden_service.dto.UserResponse;
import com.arka.orden_service.dto.VentaDto;
import com.arka.orden_service.messages.PublisherOrderCancelled;
import com.arka.orden_service.model.DetalleOrden;
import com.arka.orden_service.model.Estado;
import com.arka.orden_service.model.EstadoEnvio;
import com.arka.orden_service.model.Orden;
import com.arka.orden_service.repository.OrdenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrdenService {

    private final OrdenRepository repository;
    private final UsuarioDireccionService usuarioDireccionService;
    private final PublisherOrderCancelled publisherOrderCancelled;
    private final NotificationsComponent notificationsComponent;
    private final ObtenerEmailUsuario obtenerEmailUsuario;

    public OrdenService(OrdenRepository repository, UsuarioDireccionService usuarioDireccionService, PublisherOrderCancelled publisherOrderCancelled, NotificationsComponent notificationsComponent, ObtenerEmailUsuario obtenerEmailUsuario) {
        this.repository = repository;
        this.usuarioDireccionService = usuarioDireccionService;
        this.publisherOrderCancelled = publisherOrderCancelled;
        this.notificationsComponent = notificationsComponent;
        this.obtenerEmailUsuario = obtenerEmailUsuario;
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

    public Mono<Orden> setEstado(Integer idOrden, Estado estado){
        return Mono.fromCallable(() -> {
            Orden orden = repository.findById(idOrden)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + idOrden));

            if (estado.equals(Estado.confirmada)) {
                if (orden.getEstado().equals(Estado.confirmada)) {
                    throw new IllegalArgumentException("Order was already confirmed");
                }
                if (orden.getEstado().equals(Estado.cancelada)) {
                    throw new IllegalArgumentException("Order was already cancelled and cannot be reactivated");
                }


                orden.setEstado(Estado.confirmada);
                orden.setEstadoEnvio(EstadoEnvio.preparando);
                repository.save(orden);
                List<DetalleOrdenDto>detalles =new ArrayList<>();
                orden.getDetalles()
                        .forEach(detalleOrden -> {
                            DetalleOrdenDto detalleOrdenDto=new DetalleOrdenDto(
                                    detalleOrden.getIdProducto(),
                                    detalleOrden.getCantidad(),
                                    detalleOrden.getPrecioUnitario(),
                                    detalleOrden.getSubtotal()
                            );
                            detalles.add(detalleOrdenDto);
                        });
                VentaDto venta=new VentaDto(detalles);
                notificationsComponent.notificacionConfirmacion(venta);

            } else if (estado.equals(Estado.cancelada)) {
                if (orden.getEstadoEnvio().equals(EstadoEnvio.en_camino) ||
                        orden.getEstadoEnvio().equals(EstadoEnvio.entregado)) {
                    throw new IllegalArgumentException("The order can't be cancelled because it was already sent");
                }
                orden.setEstado(Estado.cancelada);

                publisherOrderCancelled.publisherOrderCancelled(orden);
                return repository.save(orden);
            }

            return orden;

        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public Mono<Orden> setEstadoEnvio(Integer idOrden, EstadoEnvio nuevoEstado) {
        return Mono.fromCallable(() -> {
            Orden orden = repository.findById(idOrden)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + idOrden));

            if (!orden.getEstado().equals(Estado.confirmada)) {
                throw new IllegalArgumentException("Cannot change shipping status of unconfirmed order");
            }

            EstadoEnvio estadoActual = orden.getEstadoEnvio();

            if (estadoActual.equals(EstadoEnvio.recibido)) {
                throw new IllegalArgumentException("Order must be confirmed first");
            }

            if (estadoActual.equals(EstadoEnvio.entregado)) {
                throw new IllegalArgumentException("Order already delivered, cannot change status");
            }

            if (estadoActual.equals(EstadoEnvio.en_camino) && nuevoEstado.equals(EstadoEnvio.preparando)) {
                throw new IllegalArgumentException("Cannot go back to PREPARANDO from EN_CAMINO");
            }

            orden.setEstadoEnvio(nuevoEstado);
            Orden ordenGuardada = repository.save(orden);

            UserResponse userResponse = obtenerEmailUsuario.obtenerUsuario(orden.getIdUsuario());
            if (userResponse != null) {
                if (nuevoEstado==EstadoEnvio.entregado){setEstado(orden.getId(),Estado.terminada);}
                notificationsComponent.notificacionEstadoEnvio(userResponse, orden.getId(), nuevoEstado);
            }

            return ordenGuardada;

        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public Mono<Orden> cambiarDireccion(Integer idOrden, Integer idDireccion) {
        return Mono.fromCallable(() -> {

                    Orden orden = repository.findById(idOrden)
                            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + idOrden));

                    if (!orden.getEstado().equals(Estado.pendiente)||!orden.getEstado().equals(Estado.confirmada)){
                        throw new IllegalArgumentException("Cannot change address of a confirmed or cancelled order");
                    }

                    if (!orden.getEstadoEnvio().equals(EstadoEnvio.recibido)||!orden.getEstadoEnvio().equals(EstadoEnvio.preparando)){
                        throw new IllegalArgumentException("Cannot change address, order is already being processed");
                    }

                    return orden;

                }).subscribeOn(Schedulers.boundedElastic())
                .flatMap(orden -> {
                    return usuarioDireccionService.obtenerDireccionDeUsuarioporIdDireccion(orden.getIdUsuario(), idDireccion)
                            .flatMap(direccion -> Mono.fromCallable(() -> {

                                orden.setPais(direccion.getPais());
                                orden.setCiudad(direccion.getCiudad());
                                orden.setDireccion(direccion.getDireccion());

                                return repository.save(orden);

                            }).subscribeOn(Schedulers.boundedElastic()));
                });

    }
}
