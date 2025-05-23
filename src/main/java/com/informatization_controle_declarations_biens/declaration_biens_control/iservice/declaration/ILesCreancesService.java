package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.LesCreances;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.LesCreancesProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;

import java.util.List;

public interface ILesCreancesService extends IGenericService<LesCreances, Long> {
    List<LesCreancesProjection> getByDeclaration(Long declarationId);
    // Dans ILesCreancesService.java
List<LesCreancesProjection> findByDebiteur(Long debiteurId);
}
