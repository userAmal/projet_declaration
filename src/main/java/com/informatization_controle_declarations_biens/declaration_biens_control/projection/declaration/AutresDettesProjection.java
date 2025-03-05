package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

public interface AutresDettesProjection {
    Long getId();
    Vocabulaire getCreanciers();
    float getMontant();
    Vocabulaire getJustificatifs();
    Vocabulaire getAutresPrecisions();
    LocalDate getDateCreation();
    boolean isSynthese();
    Declaration getIdDeclaration();
}
