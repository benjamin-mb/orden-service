package com.arka.orden_service.repository;

import com.arka.orden_service.model.Estado;
import com.arka.orden_service.model.EstadoEnvio;
import com.arka.orden_service.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden,Integer> {
    List<Orden>findByIdUsuario(Integer idUsuario);
    Optional<Orden> findByIdUsuarioAndEstado(Integer idUsuario, Estado estado);
    List<Orden> findAllByEstadoEnvio(EstadoEnvio estadoEnvio);
}
