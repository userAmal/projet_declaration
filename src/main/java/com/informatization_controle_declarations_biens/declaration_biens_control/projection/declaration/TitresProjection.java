package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

public interface TitresProjection {
    Long getId();
    Vocabulaire getDesignationNatureActions();
    float getValeurEmplacement();
    Vocabulaire getEmplacement();
    Vocabulaire getAutrePrecisions();
    LocalDate getDateCreation();
    boolean isSynthese();
    Declaration getIdDeclaration();
    String getFileName();
    String getFileType();

}