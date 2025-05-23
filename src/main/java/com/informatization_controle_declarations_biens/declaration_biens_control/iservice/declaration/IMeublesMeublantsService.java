package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.MeublesMeublants;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.MeublesMeublantsProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;

import java.util.List;

public interface IMeublesMeublantsService extends IGenericService<MeublesMeublants, Long> {
    List<MeublesMeublantsProjection> getByDeclaration(Long declarationId);
    List<MeublesMeublants> searchByDesignation(String designation);

}
