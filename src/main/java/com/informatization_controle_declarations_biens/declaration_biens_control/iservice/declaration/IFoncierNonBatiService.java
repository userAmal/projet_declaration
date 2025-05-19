package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.PredictionResult;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierNonBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierNonBatiProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;

import java.util.List;

public interface IFoncierNonBatiService extends IGenericService<FoncierNonBati, Long> {
    List<FoncierNonBatiProjection> getByDeclaration(Long declarationId);
    List<FoncierNonBati> findByNatureId(Long natureId);
    double getPrediction(FoncierNonBati foncierNomBati);
    byte[] generatePdfRapportNonBati(List<PredictionResult> results);
    List<FoncierNonBati> getFullEntitiesByDeclaration(Long declarationId);


}
