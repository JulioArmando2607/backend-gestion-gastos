package com.gestion.gastos.repository;

import com.gestion.gastos.model.entity.MovimientoPersonalizadoEntity;
import com.gestion.gastos.model.entity.ProyeccionMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProyeccionMensualRepository extends JpaRepository<ProyeccionMensual, Integer> {
    Optional<ProyeccionMensual> findByUsuarioIdAndAnioAndMes(Integer usuarioId, Integer anio, Integer mes);
    boolean existsByUsuarioIdAndAnioAndMes(Integer usuarioId, Integer anio, Integer mes);
}
