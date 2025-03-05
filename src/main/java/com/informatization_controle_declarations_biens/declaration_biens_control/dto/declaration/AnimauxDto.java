package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AnimauxProjection;

import lombok.Data;

@Data
public class AnimauxDto {

    public AnimauxDto() {
    }

    public AnimauxDto(AnimauxProjection projection) {
        this.id = projection.getId();
        this.especes = projection.getEspeces();
        this.nombreTetes = projection.getNombreTetes();
        this.modeAcquisition = projection.getModeAcquisition();
        this.anneeAcquisition = projection.getAnneeAcquisition();
        this.valeurAcquisition = projection.getValeurAcquisition();
        this.localite = projection.getLocalite();
        this.dateCreation = projection.getDateCreation();
        this.isSynthese = projection.isSynthese();
        this.idDeclaration = projection.getIdDeclaration();
    }

    private Long id;
    private String especes;
    private int nombreTetes;
    private Vocabulaire modeAcquisition;
    private int anneeAcquisition;
    private float valeurAcquisition;
    private Vocabulaire localite;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
}
