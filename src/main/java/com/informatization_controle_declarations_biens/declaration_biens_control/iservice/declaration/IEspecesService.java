package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Especes;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EspecesProjection;

import java.util.List;

public interface IEspecesService extends IGenericService<Especes, Long> {
    List<EspecesProjection> getByDeclaration(Long declarationId);
    // Dans IEspecesService.java
List<EspecesProjection> findByMonnaie(Float monnaie);
}
