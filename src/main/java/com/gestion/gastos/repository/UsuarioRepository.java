package com.gestion.gastos.repository;

import com.gestion.gastos.model.dto.proyección.MovimientoPersonalizadoView;
import com.gestion.gastos.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNombreIgnoreCase(String nombre);

    @Query(value = """
           SELECT COUNT(*) FROM habilitar_botones WHERE codigo_boton =:codigoBoton and habilitado = 1
           """,
            nativeQuery = true)
    Integer mostrarBoton(@Param("codigoBoton") String codigoBoton );

}
