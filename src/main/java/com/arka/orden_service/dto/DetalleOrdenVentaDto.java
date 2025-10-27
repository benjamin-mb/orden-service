package com.arka.orden_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleOrdenVentaDto {
    private Integer idProducto;
    private Integer cantidad;
    private Integer precioUnitario;
    private Integer subtotal;
    private String estado;
}
