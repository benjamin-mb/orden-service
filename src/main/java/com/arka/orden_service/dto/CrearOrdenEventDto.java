package com.arka.orden_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrearOrdenEventDto {
    private Integer idUsuario;
    private Integer montoTotal;
    private LocalDateTime fechaCreacion;
    private Integer idCarrito;
    private List<DetalleOrdenDto> detalles;
}
