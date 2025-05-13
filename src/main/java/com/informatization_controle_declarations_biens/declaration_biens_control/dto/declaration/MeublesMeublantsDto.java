package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.MeublesMeublantsProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.MeublesMeublants;

import lombok.Data;

@Data
public class MeublesMeublantsDto {

    public MeublesMeublantsDto() {
    }

    public MeublesMeublantsDto(MeublesMeublantsProjection projection) {
        this.id = projection.getId();
        this.designation = projection.getDesignation();
        this.anneeAcquisition = projection.getAnneeAcquisition();
        this.valeurAcquisition = projection.getValeurAcquisition();
        this.etatGeneral = projection.getEtatGeneral();
        this.dateCreation = projection.getDateCreation();
        this.isSynthese = projection.isSynthese();
        this.idDeclaration = projection.getidDeclaration();
        this.fileName = projection.getFileName();
        this.fileType = projection.getFileType();
    }
    public MeublesMeublantsDto(MeublesMeublants meublesMeublants) {
        this.id = meublesMeublants.getId();
        this.designation = meublesMeublants.getDesignation();
        this.anneeAcquisition = meublesMeublants.getAnneeAcquisition();
        this.valeurAcquisition = meublesMeublants.getValeurAcquisition();
        this.etatGeneral = meublesMeublants.getEtatGeneral();
        this.dateCreation = meublesMeublants.getDateCreation();
        this.isSynthese = meublesMeublants.isSynthese();
        this.idDeclaration = meublesMeublants.getIdDeclaration();
        this.fileName = meublesMeublants.getFileName();
        this.fileType = meublesMeublants.getFileType();
                if (meublesMeublants.getFileName() != null) {
            this.fileDownloadUri = "/api/foncier-bati/download/" + meublesMeublants.getId();
        }
    }
    private Long id;
    private Vocabulaire designation;
    private int anneeAcquisition;
    private float valeurAcquisition;
    private Vocabulaire etatGeneral;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
    private String fileName;
    private String fileType;
    private String fileDownloadUri; 
        @JsonIgnore
    private byte[] fileData;
}
