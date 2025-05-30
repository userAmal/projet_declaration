package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import lombok.Builder;
import lombok.Data;

// DTO correspondant
@Data
@Builder
public class ConseillerGlobalStatsDTO {
    private Long conseillerId;
    private String conseillerNom;
    private String conseillerPrenom;
    private long declarationsAssignees;
    private long declarationsTraitees;
    private double tempsTraitementMoyen;
    private long rapportsGeneres;
    private long observationsRealisees;
}

