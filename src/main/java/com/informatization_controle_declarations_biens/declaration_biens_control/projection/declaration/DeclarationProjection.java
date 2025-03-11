package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;

public interface DeclarationProjection {
    Long getId();
    LocalDate getDateDeclaration();
    Assujetti getAssujetti();
      TypeDeclarationEnum getTypeDeclarationEnum();
  EtatDeclarationEnum getEtatDeclarationEnum();
}
