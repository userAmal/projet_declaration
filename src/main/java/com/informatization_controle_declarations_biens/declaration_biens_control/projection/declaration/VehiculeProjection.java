package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

public interface VehiculeProjection {
    Long getId();
    Vocabulaire getDesignation();
    Vocabulaire getMarque();
    String getImmatriculation();
    int getAnneeAcquisition();
    float getValeurAcquisition();
    Vocabulaire getEtatGeneral();
    double getKilometrage();
    LocalDate getDateCreation();
    boolean isSynthese();
    Declaration getIdDeclaration();
    String getFileName();
    String getFileType();

}
