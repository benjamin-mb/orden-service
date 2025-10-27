package com.arka.orden_service.controller;

import com.arka.orden_service.model.Orden;
import com.arka.orden_service.service.OrdenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orden")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PatchMapping("/Estado")
    public Mono<ResponseEntity<Orden>>cambiarEstadoOrden(@RequestParam Integer idOrden, @RequestParam String estado){
        return ordenService.setEstado(idOrden,estado)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/EstadoEnvio")
    public Mono<ResponseEntity<Orden>>cambiarEstadoEnvio(@RequestParam Integer idOrden, @RequestParam String estadoEnvio){
        return ordenService.setEstadoEnvio(idOrden, estadoEnvio)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/Cambio/{idOrden}/Direccion/{idDireccion}")
    public Mono<ResponseEntity<Orden>>cambiarDireccionEntrega(@PathVariable Integer idOrden,@PathVariable Integer idDireccion){
        return ordenService.cambiarDireccion(idOrden,idDireccion)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{idOrden}")
    public Mono<ResponseEntity<Orden>>obtenerOrdenPorId(@PathVariable Integer idOrden){
        return ordenService.obtenerOrden(idOrden)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public Flux<Orden>obtenerOrdenPorIdUsuario(@PathVariable Integer idUsuario){
        return ordenService.obtenerOrdenesPorUsuario(idUsuario);
    }

    @GetMapping("/All/Estado/{estado}")
    public Flux<Orden>obtenerTodasLasOrdenesPorEstado(@PathVariable String estado){
        return ordenService.obteneTodasPorEstadoEnvio(estado);
    }
    @GetMapping()
    public Flux<Orden>obtenerTodasLasDirecciones(){
        return ordenService.obtenerTodasLasOrdenes();

    }

}
