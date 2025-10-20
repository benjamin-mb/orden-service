package com.arka.orden_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenNotificacionCambioEstadoSDTO {
    private Integer idOrden;
    private String email;
    private String nombreUsuario;
    private String estadoEnvio;
}
