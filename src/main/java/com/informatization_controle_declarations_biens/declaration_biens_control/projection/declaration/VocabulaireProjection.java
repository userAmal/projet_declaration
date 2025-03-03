package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeVocabulaire;


public interface VocabulaireProjection {
    Long getId();
    String getIntitule();
    String getVocabulaireParentIntitule();
    TypeVocabulaire getTypevocabulaire();
}