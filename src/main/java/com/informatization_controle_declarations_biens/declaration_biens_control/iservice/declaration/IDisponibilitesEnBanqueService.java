package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.DisponibilitesEnBanque;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.DisponibilitesEnBanqueProjection;

import java.util.List;

public interface IDisponibilitesEnBanqueService extends IGenericService<DisponibilitesEnBanque, Long> {
    List<DisponibilitesEnBanqueProjection> getByDeclaration(Long declarationId);
    // Dans IDisponibilitesEnBanqueService.java
List<DisponibilitesEnBanqueProjection> findByBanque(Long banqueId);
}
