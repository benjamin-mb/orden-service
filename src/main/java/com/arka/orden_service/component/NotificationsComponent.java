package com.arka.orden_service.component;


import com.arka.orden_service.dto.*;
import com.arka.orden_service.model.EstadoEnvio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificationsComponent {
    private final Logger log= LoggerFactory.getLogger(NotificationsComponent.class);
    private final RestClient restClient;
    private final String urlWeebhookEstadoEnvio;
    private final String urlWeebhookConfrimacionDeOrden;


    public NotificationsComponent(RestClient.Builder restClient,
                                  @Value("${notificacion.webhook.estado-envio.url}") String urlWeebhookEstadoEnvio,
                                  @Value("${notificacion.webhook.confirmacion-orden.url}") String urlWeebhookConfrimacionDeOrden) {
        this.restClient = restClient.build();
        this.urlWeebhookEstadoEnvio = urlWeebhookEstadoEnvio;
        this.urlWeebhookConfrimacionDeOrden = urlWeebhookConfrimacionDeOrden;
    }

    public void notificacionEstadoEnvio(UserResponse userResponse, Integer idOrden, EstadoEnvio estadoEnvio){
        try {
            OrdenNotificacionCambioEstadoSDTO ordenNoti = new OrdenNotificacionCambioEstadoSDTO(idOrden, userResponse.getEmail(), userResponse.getNombre(), estadoEnvio.name());

            restClient.post()
                    .uri(urlWeebhookEstadoEnvio)
                    .body(ordenNoti)
                    .retrieve()
                    .toBodilessEntity();
            log.info("enviado exitosamente");
        } catch (Exception e) {
            log.error("error: "+ e);
        }
    }

    public void notificacionConfirmacion(VentaDto dto){
        try{
            restClient.post()
                    .uri(urlWeebhookConfrimacionDeOrden)
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
            log.info("enviado exitosamnete");
        }catch (Exception e){
            log.error("eror:" +e);
        }
    }
}
