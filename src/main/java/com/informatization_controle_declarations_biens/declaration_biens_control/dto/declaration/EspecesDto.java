package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Especes;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EspecesProjection;


import lombok.Data;

@Data
public class EspecesDto {

    public EspecesDto() {
    }

    public EspecesDto(EspecesProjection projection) {
        this.id = projection.getId();
        this.monnaie = projection.getMonnaie();
        this.devise = projection.getDevise();
        this.tauxChange = projection.getTauxChange();
        this.montantFCFA = projection.getMontantFCFA();
        this.montantTotalFCFA = projection.getMontantTotalFCFA();
        this.dateEspece = projection.getDateEspece();
        this.dateCreation = projection.getDateCreation();
        this.isSynthese = projection.isSynthese();
        this.idDeclaration = projection.getIdDeclaration();

    }
    public EspecesDto(Especes especes) {
        this.id = especes.getId();
        this.monnaie = especes.getMonnaie();
        this.devise = especes.getDevise();
        this.tauxChange = especes.getTauxChange();
        this.montantFCFA = especes.getMontantFCFA();
        this.montantTotalFCFA = especes.getMontantTotalFCFA();
        this.dateEspece = especes.getDateEspece();
        this.dateCreation = especes.getDateCreation();
        this.isSynthese = especes.isSynthese();
        this.idDeclaration = especes.getIdDeclaration();

    }
    private Long id;
    private float monnaie;
    private float devise;
    private float tauxChange;
    private float montantFCFA;
    private float montantTotalFCFA;
    private LocalDate dateEspece;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
}
