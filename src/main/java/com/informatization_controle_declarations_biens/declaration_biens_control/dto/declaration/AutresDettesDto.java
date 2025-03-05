package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresDettesProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
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
    }

    private Long id;
    private Vocabulaire creanciers;
    private float montant;
    private Vocabulaire justificatifs;
    private Vocabulaire autresPrecisions;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
}
