package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierNonBatiProjection;

import lombok.Data;

@Data
public class FoncierNonBatiDto {

    public FoncierNonBatiDto() {
    }

    public FoncierNonBatiDto(FoncierNonBatiProjection projection) {
        this.id = projection.getId();
        this.nature = projection.getNature();
        this.modeAcquisition = projection.getModeAcquisition();
        this.ilot = projection.getIlot();
        this.lotissement = projection.getLotissement();
        this.superficie = projection.getSuperficie();
        this.localite = projection.getLocalite();
        this.titrePropriete = projection.getTitrePropriete();
        this.dateAcquis = projection.getDateAcquis();
        this.valeurAcquisFCFA = projection.getValeurAcquisFCFA();
        this.coutInvestissements = projection.getCoutInvestissements();
        this.dateCreation = projection.getDateCreation();
        this.isSynthese = projection.isSynthese();
        this.idDeclaration = projection.getIdDeclaration();
    }

    private Long id;
    private Vocabulaire nature;
    private Vocabulaire modeAcquisition;
    private String ilot;
    private String lotissement;
    private String superficie;
    private String localite;
    private String titrePropriete;
    private int dateAcquis;
    private float valeurAcquisFCFA;
    private float coutInvestissements;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
}
