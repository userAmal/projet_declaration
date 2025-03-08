package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AppareilsElectroMenagers;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AppareilsElectroMenagersProjection;

import lombok.Data;

@Data
public class AppareilsElectroMenagersDto {

    public AppareilsElectroMenagersDto() {
    }

    public AppareilsElectroMenagersDto(AppareilsElectroMenagersProjection projection) {
        this.id = projection.getId();
        this.designation = projection.getDesignation();
        this.anneeAcquisition = projection.getAnneeAcquisition();
        this.valeurAcquisition = projection.getValeurAcquisition();
        this.etatGeneral = projection.getEtatGeneral();
        this.dateCreation = projection.getDateCreation();
        this.isSynthese = projection.isSynthese();
        this.idDeclaration = projection.getIdDeclaration();
    }
public AppareilsElectroMenagersDto(AppareilsElectroMenagers entity) {
    this.id = entity.getId();
    this.designation = entity.getDesignation();
    this.anneeAcquisition = entity.getAnneeAcquisition();
    this.valeurAcquisition = entity.getValeurAcquisition();
    this.etatGeneral = entity.getEtatGeneral();
    this.dateCreation = entity.getDateCreation();
    this.isSynthese = entity.isSynthese();
    this.idDeclaration = entity.getIdDeclaration();
}

    private Long id;
    private Vocabulaire designation;
    private int anneeAcquisition;
    private float valeurAcquisition;
    private Vocabulaire etatGeneral;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
}
