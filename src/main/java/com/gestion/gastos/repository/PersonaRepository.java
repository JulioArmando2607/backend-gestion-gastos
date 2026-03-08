package com.gestion.gastos.repository;

import com.gestion.gastos.model.entity.Personas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Personas, Integer> {
    Optional<Personas> findByUsuarioId(Integer usuarioId);
}
