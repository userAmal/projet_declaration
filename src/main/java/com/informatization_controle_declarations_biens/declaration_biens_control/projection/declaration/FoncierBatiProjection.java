package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

public interface FoncierBatiProjection {
    Long getId();
    Vocabulaire getNature();
    int getAnneeConstruction();
    Vocabulaire getModeAcquisition();
    String getReferencesCadastrales();
    String getSuperficie();
    Vocabulaire getLocalis();
    Vocabulaire getTypeUsage();
    float getCoutAcquisitionFCFA();
    float getCoutInvestissements();
    LocalDate getDateCreation();
    boolean isSynthese();
    Declaration getIdDeclaration();
    String getFileName();
    String getFileType();

}
