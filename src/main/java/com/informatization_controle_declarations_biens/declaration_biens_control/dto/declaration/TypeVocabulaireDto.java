package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;


import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TypeVocabulaireProjection;
import lombok.Data;

@Data
public class TypeVocabulaireDto {




    public TypeVocabulaireDto() {
    }

    public TypeVocabulaireDto(TypeVocabulaireProjection typeVocabulaireProjection) {
        this.id = typeVocabulaireProjection.getId();
        this.nom = typeVocabulaireProjection.getIntitule();
    }
    private Long id;
    private String nom;
}

