package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

public interface EmpruntsProjection {
    Long getId();
    Vocabulaire getInstitutionsFinancieres();
    String getNumeroCompte();
    Vocabulaire getTypeEmprunt();
    float getMontantEmprunt();
    float getMontantRestant();
    LocalDate getDateCreation();
    boolean isSynthese();
    Declaration getIdDeclaration();
    String getFileName();
    String getFileType();
}
