package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

public interface LesCreancesProjection {
    Long getId();
    Vocabulaire getDebiteurs();
    float getMontant();
    Vocabulaire getAutresPrecisions();
    LocalDate getDateCreation();
    boolean isSynthese();
    Declaration getIdDeclaration();
    String getFileName();
    String getFileType();

}
