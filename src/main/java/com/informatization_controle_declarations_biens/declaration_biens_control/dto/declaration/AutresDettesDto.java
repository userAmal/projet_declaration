package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresDettesProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresDettes;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import lombok.Data;

@Data
public class AutresDettesDto {

    public AutresDettesDto() {
    }

    public AutresDettesDto(AutresDettesProjection projection) {
        this.id = projection.getId();
        this.creanciers = projection.getCreanciers();
        this.montant = projection.getMontant();
        this.justificatifs = projection.getJustificatifs();
        this.autresPrecisions = projection.getAutresPrecisions();
        this.dateCreation = projection.getDateCreation();
        this.isSynthese = projection.isSynthese();
        this.idDeclaration = projection.getIdDeclaration();
        this.fileName = projection.getFileName();
        this.fileType = projection.getFileType();

    }
    public AutresDettesDto(AutresDettes autresDettes) {
        this.id = autresDettes.getId();
        this.creanciers = autresDettes.getCreanciers();
        this.montant = autresDettes.getMontant();
        this.justificatifs = autresDettes.getJustificatifs();
        this.autresPrecisions = autresDettes.getAutresPrecisions();
        this.dateCreation = autresDettes.getDateCreation();
        this.isSynthese = autresDettes.isSynthese();
        this.idDeclaration = autresDettes.getIdDeclaration();
        this.fileName = autresDettes.getFileName();
        this.fileType = autresDettes.getFileType();
        
        // Générer l'URL de téléchargement si le fichier existe
        if (autresDettes.getFileName() != null) {
            this.fileDownloadUri = "/api/foncier-bati/download/" + autresDettes.getId();
        }

    }
    private Long id;
    private Vocabulaire creanciers;
    private float montant;
    private Vocabulaire justificatifs;
    private Vocabulaire autresPrecisions;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
      private String fileName;
    private String fileType;
    private String fileDownloadUri; 
        @JsonIgnore
    private byte[] fileData;



}
