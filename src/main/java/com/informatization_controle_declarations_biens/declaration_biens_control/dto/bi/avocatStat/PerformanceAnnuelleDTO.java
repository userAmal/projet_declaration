package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.avocatStat;

import java.util.Map;

public  class PerformanceAnnuelleDTO {
    private Map<Integer, Long> declarationsParAnnee;
    private Map<Integer, Long> conclusionsParAnnee;
    private Map<Integer, Double> tauxTraitementParAnnee;

    public PerformanceAnnuelleDTO(Map<Integer, Long> declarationsParAnnee, 
                                 Map<Integer, Long> conclusionsParAnnee, 
                                 Map<Integer, Double> tauxTraitementParAnnee) {
        this.declarationsParAnnee = declarationsParAnnee;
        this.conclusionsParAnnee = conclusionsParAnnee;
        this.tauxTraitementParAnnee = tauxTraitementParAnnee;
    }

    // Getters
    public Map<Integer, Long> getDeclarationsParAnnee() { return declarationsParAnnee; }
    public Map<Integer, Long> getConclusionsParAnnee() { return conclusionsParAnnee; }
    public Map<Integer, Double> getTauxTraitementParAnnee() { return tauxTraitementParAnnee; }
}
