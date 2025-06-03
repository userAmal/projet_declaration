package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporalStatsDto {
    private Integer periode; // mois (1-12) ou année
    private String libellePeriode;
    private Long totalDeclarations;
    private Long declarationsValidees;
    private Long declarationsRefusees;
    private Long rapportsGeneres;
    private Double tauxValidation;
    private Double tempsMoyenTraitement;
    
    // Répartition par type
    private Map<String, Long> repartitionParType;
    
    // Répartition par utilisateur
    private Map<String, Long> repartitionParUtilisateur;
}