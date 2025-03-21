package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.parametrage;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.parametrage.Parametrage;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.parametrage.ParametrageProjection;

import java.util.List;
import java.util.Optional;

public interface IParametrageService extends IGenericService<Parametrage, Long> {
    List<Parametrage> findAll();
    Optional<ParametrageProjection> findProjectedById(Long id);
    Parametrage updateValeur(Long id, String valeur);

}
