package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeclarationPrioritaireDTO {
    private Long declarationId;
    private String assujettiNom;
    private String assujettiPrenom;
    private LocalDate dateDeclaration;
    private long joursDepuisDeclaration;
    private EtatDeclarationEnum etatDeclaration;
}
