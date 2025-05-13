package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.RevenusProjection;
import lombok.Data;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Revenus;

@Data
public class RevenusDto {
    private Long id;
    private float salaireMensuelNet;
    private Vocabulaire autresRevenus;
    private LocalDate dateCreation;
    private Declaration idDeclaration;
    private String fileName;
    private String fileType;
    private String fileDownloadUri; 
        @JsonIgnore
    private byte[] fileData;

    public RevenusDto() {}

    public RevenusDto(RevenusProjection projection) {
        this.id = projection.getId();
        this.salaireMensuelNet = projection.getSalaireMensuelNet();
        this.autresRevenus = projection.getAutresRevenus();
        this.dateCreation = projection.getDateCreation();
        this.idDeclaration = projection.getIdDeclaration();
        this.fileName = projection.getFileName();
        this.fileType = projection.getFileType();
    }

    public RevenusDto(Revenus revenus) {
        this.id = revenus.getId();
        this.salaireMensuelNet = revenus.getSalaireMensuelNet();
        this.autresRevenus = revenus.getAutresRevenus();
        this.dateCreation = revenus.getDateCreation();
        this.idDeclaration = revenus.getIdDeclaration();
        this.fileName = revenus.getFileName();
        this.fileType = revenus.getFileType();
                if (revenus.getFileName() != null) {
            this.fileDownloadUri = "/api/foncier-bati/download/" + revenus.getId();
        }
    }
}
