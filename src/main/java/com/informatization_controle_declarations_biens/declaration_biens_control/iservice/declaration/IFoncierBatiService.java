package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.PredictionResult;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierBatiProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;

import java.util.List;

public interface IFoncierBatiService extends IGenericService<FoncierBati, Long> {
    List<FoncierBatiProjection> getByDeclaration(Long declarationId);
    double getPrediction(FoncierBati foncierBati);
    List<FoncierBati> getFullEntitiesByDeclaration(Long declarationId);
    byte[] generatePdfRapport(List<PredictionResult> results);


}
