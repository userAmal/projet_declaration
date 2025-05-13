package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Revenus;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.RevenusProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;

import java.util.List;

public interface IRevenusService extends IGenericService<Revenus, Long> {
    List<RevenusProjection> getByDeclaration(Long declarationId);

    // Dans IRevenusService.java
List<RevenusProjection> findByAutresRevenus(Long autresRevenusId);
}
