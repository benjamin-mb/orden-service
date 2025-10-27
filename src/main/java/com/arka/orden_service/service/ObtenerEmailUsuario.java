package com.arka.orden_service.service;

import com.arka.orden_service.dto.UserResponse;
import com.arka.orden_service.exceptions.EmailNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ObtenerEmailUsuario {

    private static final Logger log= LoggerFactory.getLogger(ObtenerEmailUsuario.class);
    private final RestClient restClient;

    public ObtenerEmailUsuario(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://USUARIO-SERVICE")
                .build();
    }

    public UserResponse obtenerUsuario(Integer idUsuario){
        try{
            UserResponse userResponse=restClient.get()
                    .uri("api/usuarios/{idUsuario}",idUsuario)
                    .retrieve()
                    .body(UserResponse.class);
                    log.info("recolect user properly "+userResponse);
                    return userResponse;
        } catch (Exception e) {
            log.error("error: "+e);
            throw new EmailNotFoundException("email not found ");
        }
    }
}
