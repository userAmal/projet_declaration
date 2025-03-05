package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

public interface AnimauxProjection {
    Long getId();
    String getEspeces();
    int getNombreTetes();
    Vocabulaire getModeAcquisition();
    int getAnneeAcquisition();
    float getValeurAcquisition();
    Vocabulaire getLocalite();
    LocalDate getDateCreation();
    boolean isSynthese();
    Declaration getIdDeclaration();
}
