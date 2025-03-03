package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VocabulaireProjection;
import lombok.Data;

@Data
public class VocabulaireDto {
    private Long id;
    private String intitule;
    private String vocabulaireParentIntitule; 
    private TypeVocabulaireDto typeVocabulaire; 
    
    public VocabulaireDto() {
    }

    public VocabulaireDto(VocabulaireProjection vocabulaireProjection) {
        this.id = vocabulaireProjection.getId();
        this.intitule = vocabulaireProjection.getIntitule();
        this.vocabulaireParentIntitule = vocabulaireProjection.getVocabulaireParentIntitule();
        
        if (vocabulaireProjection.getTypevocabulaire() != null) {
            TypeVocabulaireDto typeDto = new TypeVocabulaireDto();
            typeDto.setId(vocabulaireProjection.getTypevocabulaire().getId());
            typeDto.setIntitule(vocabulaireProjection.getTypevocabulaire().getIntitule());
            this.typeVocabulaire = typeDto;
        }
    }
}
