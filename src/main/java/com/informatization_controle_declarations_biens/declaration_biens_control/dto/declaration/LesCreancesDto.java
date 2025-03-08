package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.LesCreancesProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.LesCreances;

import lombok.Data;

@Data
public class LesCreancesDto {

    public LesCreancesDto() {
    }

    public LesCreancesDto(LesCreancesProjection projection) {
        this.id = projection.getId();
        this.debiteurs = projection.getDebiteurs();
        this.montant = projection.getMontant();
        this.autresPrecisions = projection.getAutresPrecisions();
        this.dateCreation = projection.getDateCreation();
        this.isSynthese = projection.isSynthese();
        this.idDeclaration = projection.getIdDeclaration();
    }
    public LesCreancesDto(LesCreances lesCreances) {
        this.id = lesCreances.getId();
        this.debiteurs = lesCreances.getDebiteurs();
        this.montant = lesCreances.getMontant();
        this.autresPrecisions = lesCreances.getAutresPrecisions();
        this.dateCreation = lesCreances.getDateCreation();
        this.isSynthese = lesCreances.isSynthese();
        this.idDeclaration = lesCreances.getIdDeclaration();
    }

    private Long id;
    private Vocabulaire debiteurs;
    private float montant;
    private Vocabulaire autresPrecisions;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
}
