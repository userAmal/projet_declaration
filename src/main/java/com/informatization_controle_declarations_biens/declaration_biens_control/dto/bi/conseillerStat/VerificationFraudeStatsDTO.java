package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationFraudeStatsDTO {
    private Long declarationId;
    private Long conseillerId;
    private int scoreRisque;
    private List<String> anomaliesDetectees;
    private LocalDate dateVerification;
    private boolean necessiteVerificationApprofondie;
    private String assujettiInfo;
}
