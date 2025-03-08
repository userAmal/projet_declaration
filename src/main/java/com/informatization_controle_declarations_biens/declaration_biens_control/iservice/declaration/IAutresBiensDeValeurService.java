package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresBiensDeValeur;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresBiensDeValeurProjection;

import java.util.List;
import java.util.Optional;

public interface IAutresBiensDeValeurService {
    List<AutresBiensDeValeurProjection> getByDeclaration(Long declarationId);
    Optional<AutresBiensDeValeur> findById(Long id);
    AutresBiensDeValeur save(AutresBiensDeValeur entity);

}
