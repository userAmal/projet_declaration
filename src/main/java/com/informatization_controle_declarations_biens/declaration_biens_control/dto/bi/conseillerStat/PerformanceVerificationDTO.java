package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceVerificationDTO {
    private long totalRapportsGeneres;
    private long totalObservations;
    private double moyenneObservationsParRapport;
    private double efficaciteTraitement;
}