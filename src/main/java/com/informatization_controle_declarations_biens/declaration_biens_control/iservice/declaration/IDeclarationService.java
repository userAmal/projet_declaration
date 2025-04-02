package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;



import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;


public interface IDeclarationService extends IGenericService<Declaration, Long> {
 DeclarationDto getFullDeclarationDetails(Long declarationId);
 Declaration validateDeclaration(Long id);
 Declaration refuseDeclaration(Long id);
}