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
@AllArgsConstructor
@NoArgsConstructor
public class DeclarationConseillerDTO {
    private Long declarationId;
    private String assujettiNom;
    private String assujettiPrenom;
    private LocalDate dateDeclaration;
    private TypeDeclarationEnum typeDeclaration;
    private EtatDeclarationEnum etatDeclaration;
    private LocalDate dateAffectation;
    private boolean rapportProvisoireGenere;
    private long nombreObservations;
    private long joursTraitement;
}
