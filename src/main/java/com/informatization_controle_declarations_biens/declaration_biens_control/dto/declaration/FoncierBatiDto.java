package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierBatiProjection;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoncierBatiDto {
    private Long id;
    private Vocabulaire nature;
    private int anneeConstruction;
    private Vocabulaire modeAcquisition;
    private String referencesCadastrales;
    private String superficie;
    private Vocabulaire localis;
    private Vocabulaire typeUsage;
    private float coutAcquisitionFCFA;
    private float coutInvestissements;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
    
    // Champs pour les fichiers
    private String fileName;
    private String fileType;
    private String fileDownloadUri; // URL pour télécharger le fichier
    
    // On exclut le contenu du fichier du DTO pour des raisons de sécurité et performance
    @JsonIgnore
    private byte[] fileData;

    public FoncierBatiDto() {
    }

    public FoncierBatiDto(FoncierBatiProjection projection) {
        this.id = projection.getId();
        this.nature = projection.getNature();
        this.anneeConstruction = projection.getAnneeConstruction();
        this.modeAcquisition = projection.getModeAcquisition();
        this.referencesCadastrales = projection.getReferencesCadastrales();
        this.superficie = projection.getSuperficie();
        this.localis = projection.getLocalis();
        this.typeUsage = projection.getTypeUsage();
        this.coutAcquisitionFCFA = projection.getCoutAcquisitionFCFA();
        this.coutInvestissements = projection.getCoutInvestissements();
        this.dateCreation = projection.getDateCreation();
        this.isSynthese = projection.isSynthese();
        this.idDeclaration = projection.getIdDeclaration();
        this.fileName = projection.getFileName();
        this.fileType = projection.getFileType();
    }

    public FoncierBatiDto(FoncierBati foncierBati) {
        this.id = foncierBati.getId();
        this.nature = foncierBati.getNature();
        this.anneeConstruction = foncierBati.getAnneeConstruction();
        this.modeAcquisition = foncierBati.getModeAcquisition();
        this.referencesCadastrales = foncierBati.getReferencesCadastrales();
        this.superficie = foncierBati.getSuperficie();
        this.localis = foncierBati.getLocalis();
        this.typeUsage = foncierBati.getTypeUsage();
        this.coutAcquisitionFCFA = foncierBati.getCoutAcquisitionFCFA();
        this.coutInvestissements = foncierBati.getCoutInvestissements();
        this.dateCreation = foncierBati.getDateCreation();
        this.isSynthese = foncierBati.isSynthese();
        this.idDeclaration = foncierBati.getIdDeclaration();
        this.fileName = foncierBati.getFileName();
        this.fileType = foncierBati.getFileType();
        
        // Générer l'URL de téléchargement si le fichier existe
        if (foncierBati.getFileName() != null) {
            this.fileDownloadUri = "/api/foncier-bati/download/" + foncierBati.getId();
        }
    }
}