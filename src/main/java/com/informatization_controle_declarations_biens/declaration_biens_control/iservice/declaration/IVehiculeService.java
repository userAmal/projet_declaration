package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vehicule;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VehiculeProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;

import java.util.List;

public interface IVehiculeService extends IGenericService<Vehicule, Long> {
    List<VehiculeProjection> getByDeclaration(Long declarationId);
    double getPrediction(Vehicule vehicule);

}
