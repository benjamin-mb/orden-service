package com.arka.orden_service.repository;

import com.arka.orden_service.model.Estado;
import com.arka.orden_service.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden,Integer> {
    List<Orden>findByIdUsuario(Integer idUsuario);
    List<Orden>findByIdUsuaroAndEstado(Integer idUsuario, Estado estado);
}
