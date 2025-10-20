package com.arka.orden_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Integer id;

    @Column(name="id_usuario")
    private Integer idUsuario;

    @Column(name = "id_carrito")
    private Integer idCarrito;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('pendiente','confirmada','cancelada')")
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_envio", columnDefinition = "ENUM('PREPARANDO','EN_CAMINO','ENTREGADO','DEVUELTO')")
    private EstadoEnvio estadoEnvio;

    @Column(name = "monto_total")
    private Integer montoTotal;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "ciudad")
    private String ciudad;

    @Column( name = "pais")
    private  String pais;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleOrden> detalles = new ArrayList<>();

    public Orden(Integer idUsuario, Integer idCarrito, LocalDateTime fecha, Integer montoTotal) {
        this.idUsuario = idUsuario;
        this.idCarrito = idCarrito;
        this.fecha = fecha;
        this.estado = Estado.pendiente;
        this.estadoEnvio = EstadoEnvio.recibido;
        this.montoTotal = montoTotal;
    }

    public void addDetalle(DetalleOrden detalleOrden){
        detalles.add(detalleOrden);
        detalleOrden.setOrden(this);
    }
}
