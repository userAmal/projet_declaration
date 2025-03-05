package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VehiculeProjection;

import lombok.Data;

@Data
public class VehiculeDto {

    public VehiculeDto() {
    }

    public VehiculeDto(VehiculeProjection projection) {
        this.id = projection.getId();
        this.designation = projection.getDesignation();
        this.marque = projection.getMarque();
        this.immatriculation = projection.getImmatriculation();
        this.anneeAcquisition = projection.getAnneeAcquisition();
        this.valeurAcquisition = projection.getValeurAcquisition();
        this.etatGeneral = projection.getEtatGeneral();
        this.dateCreation = projection.getDateCreation();
        this.isSynthese = projection.isSynthese();
        this.idDeclaration = projection.getIdDeclaration();
    }

    private Long id;
    private Vocabulaire designation;
    private Vocabulaire marque;
    private String immatriculation;
    private int anneeAcquisition;
    private float valeurAcquisition;
    private Vocabulaire etatGeneral;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
}
