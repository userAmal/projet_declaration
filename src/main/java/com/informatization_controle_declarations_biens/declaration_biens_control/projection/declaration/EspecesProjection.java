package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

public interface EspecesProjection {
    Long getId();
    float getMonnaie();
    float getDevise();
    float getTauxChange();
    float getMontantFCFA();
    float getMontantTotalFCFA();
    LocalDate getDateEspece();
    LocalDate getDateCreation();
    boolean isSynthese();
    Declaration getIdDeclaration();

}
