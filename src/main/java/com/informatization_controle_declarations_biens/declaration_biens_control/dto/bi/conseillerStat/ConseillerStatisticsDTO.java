package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConseillerStatisticsDTO {
    private long declarationsAssignees;
    private long rapportsProvisoiresGeneres;
    private long observationsRealisees;
    private long declarationsTraitees;
    private long declarationsEnCours;
    private double tempsTraitementMoyen;
    private List<StatsMensuellesDTO> statistiquesParMois;
    private List<RepartitionEtatDTO> repartitionParEtat;
    private PerformanceVerificationDTO performanceVerification;
}
