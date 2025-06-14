package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeclarationAncienneDTO {
    private Long declarationId;
    private String assujettiNom;
    private String assujettiPrenom;
    private TypeDeclarationEnum typeDeclaration;
    private EtatDeclarationEnum etatDeclaration;
    private LocalDate dateDeclaration;
    private LocalDate dateAffectation;
    private long joursAnciennete;
    private boolean rapportProvisoireExiste;
    private long nombreObservations;
    private String niveauPriorite; // CRITIQUE, URGENT, IMPORTANT, NORMAL
    private String recommandationAction;
}
