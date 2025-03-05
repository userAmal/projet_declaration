package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresBiensDeValeurProjection;
import lombok.Data;

@Data
public class AutresBiensDeValeurDto {
    private Long id;
    private Vocabulaire designation;
    private Vocabulaire localite;
    private int anneeAcquis;
    private float valeurAcquisition;
    private Vocabulaire autrePrecisions;
    private Vocabulaire type;
    private LocalDate dateCreation;
    private boolean isSynthese;

    public AutresBiensDeValeurDto() {}

    public AutresBiensDeValeurDto(AutresBiensDeValeurProjection projection) {
        this.id = projection.getId();
        this.designation = projection.getDesignation();
        this.localite = projection.getLocalite();
        this.anneeAcquis = projection.getAnneeAcquis();
        this.valeurAcquisition = projection.getValeurAcquisition();
        this.autrePrecisions = projection.getAutrePrecisions();
        this.type = projection.getType();
        this.dateCreation = projection.getDateCreation();
        this.isSynthese = projection.isSynthese();
    }
}
