
package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TitresProjection;
import lombok.Data;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Titres;

@Data
public class TitresDto {
    private Long id;
    private Vocabulaire designationNatureActions;
    private float valeurEmplacement;
    private Vocabulaire emplacement;
    private Vocabulaire autrePrecisions;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
    private String fileName;
    private String fileType;
    private String fileDownloadUri; 
        @JsonIgnore
    private byte[] fileData;
    public TitresDto() {}

    public TitresDto(TitresProjection titresProjection) {
        this.id = titresProjection.getId();
        this.designationNatureActions = titresProjection.getDesignationNatureActions();
        this.valeurEmplacement = titresProjection.getValeurEmplacement();
        this.emplacement = titresProjection.getEmplacement();
        this.autrePrecisions = titresProjection.getAutrePrecisions();
        this.dateCreation = titresProjection.getDateCreation();
        this.isSynthese = titresProjection.isSynthese();
        this.idDeclaration = titresProjection.getIdDeclaration();
        this.fileName = titresProjection.getFileName();
        this.fileType = titresProjection.getFileType();
    }
    public TitresDto(Titres titres) {
        this.id = titres.getId();
        this.designationNatureActions = titres.getDesignationNatureActions();
        this.valeurEmplacement = titres.getValeurEmplacement();
        this.emplacement = titres.getEmplacement();
        this.autrePrecisions = titres.getAutrePrecisions();
        this.dateCreation = titres.getDateCreation();
        this.isSynthese = titres.isSynthese();
        this.idDeclaration = titres.getIdDeclaration();
        this.fileName = titres.getFileName();
        this.fileType = titres.getFileType();
                if (titres.getFileName() != null) {
            this.fileDownloadUri = "/api/foncier-bati/download/" + titres.getId();
        }
    }
}
