package com.arka.orden_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenCompleta {
    private Integer idProducto;
    private Integer cantidad;
}
