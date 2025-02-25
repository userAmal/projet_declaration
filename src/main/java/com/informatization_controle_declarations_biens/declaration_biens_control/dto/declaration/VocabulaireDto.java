package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeVocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VocabulaireProjection;
import lombok.Data;

@Data
public class VocabulaireDto {



    public VocabulaireDto() {
    }

    public VocabulaireDto(VocabulaireProjection vocabulaireProjection) {
        this.id = vocabulaireProjection.getId();
        this.intitule = vocabulaireProjection.getIntitule();
        this.vocabulaireParentIntitule = vocabulaireProjection.getVocabulaireParentIntitule();
        this.typeVocabulaire = vocabulaireProjection.getTypevocabulaire();
    }
    private Long id;
    private String intitule;
    private Vocabulaire vocabulaireParentIntitule;
    private TypeVocabulaire typeVocabulaire;
}
