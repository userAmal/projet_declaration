package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Emprunts;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EmpruntsProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;

import java.util.List;

public interface IEmpruntsService extends IGenericService<Emprunts, Long> {
    List<EmpruntsProjection> getByDeclaration(Long declarationId);
    List<Emprunts> getByInstitutionFinanciere(Long vocabulaireId);

}
