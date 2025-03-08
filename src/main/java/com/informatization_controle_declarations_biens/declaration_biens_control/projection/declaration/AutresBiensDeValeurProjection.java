package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;

public interface AutresBiensDeValeurProjection {
    Long getId();
    Vocabulaire getDesignation();
    Vocabulaire getLocalite();
    int getAnneeAcquis();
    float getValeurAcquisition();
    Vocabulaire getAutrePrecisions();
    Vocabulaire getType();
    LocalDate getDateCreation();
    boolean isSynthese();
    Declaration getIdDeclaration();

}
