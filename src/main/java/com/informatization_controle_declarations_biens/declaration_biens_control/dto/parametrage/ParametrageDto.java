package com.informatization_controle_declarations_biens.declaration_biens_control.dto.parametrage;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.parametrage.Parametrage;
import lombok.Data;

@Data
public class ParametrageDto {
    private Long id;
    private String code;
    private String description;
    private String valeur;

    // Constructeur par défaut
    public ParametrageDto() {}

    // Constructeur acceptant un Parametrage
    public ParametrageDto(Parametrage parametrage) {
        this.id = parametrage.getId();
        this.code = parametrage.getCode();
        this.description = parametrage.getDescription();
        this.valeur = parametrage.getValeur();
    }
}
