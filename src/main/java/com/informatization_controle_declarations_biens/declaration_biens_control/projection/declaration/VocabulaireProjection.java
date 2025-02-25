package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeVocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;

import org.springframework.beans.factory.annotation.Value;

public interface VocabulaireProjection {

    Long getId();

    String getIntitule();

    TypeVocabulaire getTypevocabulaire();

    @Value("#{target.vocabulaireParent?.intitule}") 
    Vocabulaire getVocabulaireParentIntitule();
}
