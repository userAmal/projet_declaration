package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

public interface FoncierNonBatiProjection {
    Long getId();
    Vocabulaire getNature();
    Vocabulaire getModeAcquisition();
    String getIlot();
    String getLotissement();
    String getSuperficie();
    String getLocalite();
    String getTitrePropriete();
    LocalDate getDateAcquis();
    float getValeurAcquisFCFA();
    float getCoutInvestissements();
    LocalDate getDateCreation();
    boolean isSynthese();
    Declaration getIdDeclaration();
}
