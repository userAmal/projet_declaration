package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;

public interface MeublesMeublantsProjection {
    Long getId();
    Vocabulaire getDesignation();
    int getAnneeAcquisition();
    float getValeurAcquisition();
    Vocabulaire getEtatGeneral();
    LocalDate getDateCreation();
    boolean isSynthese();
    Declaration getidDeclaration();

}
