package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Animaux;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AnimauxProjection;

import java.util.List;

public interface IAnimauxService extends IGenericService<Animaux, Long> {
    List<AnimauxProjection> getAnimauxByDeclaration(Long declarationId);
}