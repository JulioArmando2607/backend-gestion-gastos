package com.gestion.gastos.service;

import com.gestion.gastos.model.entity.CompartidoModulo;
import com.gestion.gastos.model.entity.ModuloCompartido;
import com.gestion.gastos.model.entity.PermisoCompartido;
import com.gestion.gastos.repository.CompartidoModuloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompartidoModuloService {

    private final CompartidoModuloRepository compartidoModuloRepository;

    public boolean puedeVer(Integer idPersonaCompartida, ModuloCompartido modulo, Long recursoId) {
        return compartidoModuloRepository
                .existsByIdPersonaCompartidaAndModuloAndRecursoIdAndActivoTrue(
                        idPersonaCompartida,
                        modulo,
                        recursoId
                );
    }

    public boolean puedeEditar(Integer idPersonaCompartida, ModuloCompartido modulo, Long recursoId) {
        return compartidoModuloRepository.hasPermiso(
                idPersonaCompartida,
                modulo,
                recursoId,
                PermisoCompartido.EDITAR
        );
    }

    public List<CompartidoModulo> listarActivos(ModuloCompartido modulo, Long recursoId) {
        return compartidoModuloRepository.findActivosByModuloAndRecursoId(modulo, recursoId);
    }
}
