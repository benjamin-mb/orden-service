package com.arka.orden_service.service;

import com.arka.orden_service.dto.Direcciones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Component
public class UsuarioDireccionService {

    private static final Logger log= LoggerFactory.getLogger(UsuarioDireccionService.class);
    private final RestClient restClient;

    public UsuarioDireccionService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://usuario-service")
                .build();
    }

    public Mono<Direcciones> obtenerDireccionDeUsuario(Integer idUsuario){
        return Mono.fromCallable(() -> {
            try {
                Direcciones direccion = restClient.get()
                        .uri("/api/usuarios/{idUsuario}/direcciones/principal", idUsuario)
                        .retrieve()
                        .body(Direcciones.class);

                log.info("Dirección obtenida: {}", direccion != null ? direccion.getDireccion() : "null");
                return direccion;

            } catch (Exception e) {
                log.warn("No se pudo obtener dirección principal: {}", e.getMessage());
                return null;
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Direcciones> obtenerDireccionDeUsuarioporIdDireccion(Integer idUsuario, Integer idDireccion){
        return Mono.fromCallable(() -> {
            try {
                Direcciones direccion = restClient.get()
                        .uri("/api/usuarios/{idUsuario}/direcciones/{idDireccion}", idUsuario,idDireccion)
                        .retrieve()
                        .body(Direcciones.class);

                log.info("Dirección obtenida: ", direccion);
                return direccion;

            } catch (Exception e) {
                log.warn("No se pudo obtener dirección principal:", e.getMessage());
                return null;
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

}



