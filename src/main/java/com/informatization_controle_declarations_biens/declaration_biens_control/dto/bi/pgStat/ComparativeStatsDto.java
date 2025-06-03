package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparativeStatsDto {
    private String titre;
    private String description;
    
    // Données actuelles vs précédentes
    private StatsPeriod currentPeriod;
    private StatsPeriod previousPeriod;
    
    // Évolution
    private Double evolutionPourcentage;
    private String tendance; // HAUSSE, BAISSE, STABLE
    
    // Top performers
    private List<PerformerDto> topPerformers;
    private List<PerformerDto> underPerformers;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsPeriod {
        private String periode;
        private Long totalDeclarations;
        private Long declarationsTraitees;
        private Double tauxReussite;
        private Double tempsMoyenTraitement;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformerDto {
        private String nom;
        private Long valeur;
        private String unite;
        private Double score;
    }
}

