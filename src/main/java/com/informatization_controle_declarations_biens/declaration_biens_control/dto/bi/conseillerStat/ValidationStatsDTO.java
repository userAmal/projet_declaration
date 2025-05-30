package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationStatsDTO {
    private long totalDeclarations;
    private long declarationsValidees;
    private long declarationsRejetees;
    private double tauxValidation;
    private double tauxRejet;
}
