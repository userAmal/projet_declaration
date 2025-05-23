package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresDettes;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresDettesProjection;
import java.util.List;

public interface IAutresDettesService extends IGenericService<AutresDettes, Long> {
    List<AutresDettesProjection> getByDeclaration(Long declarationId);
    // Dans IAutresDettesService.java
List<AutresDettesProjection> findByCreancier(Long creancierId);
}