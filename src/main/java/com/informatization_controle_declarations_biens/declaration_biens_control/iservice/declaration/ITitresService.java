package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Titres;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TitresProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;

import java.util.List;

public interface ITitresService extends IGenericService<Titres, Long> {
    List<TitresProjection> getByDeclaration(Long declarationId);
    // Dans ITitresService.java
List<TitresProjection> findByDesignationNatureActions(Long designationId);
}
