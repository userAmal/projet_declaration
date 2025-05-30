package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RapportProvisoireStatsDTO {
    private Long declarationId;
    private Long conseillerId;
    private long nombreObservations;
    private boolean rapportDejaGenere;
    private LocalDate dateGeneration;
    private String assujettiInfo;
    private TypeDeclarationEnum typeDeclaration;
}
