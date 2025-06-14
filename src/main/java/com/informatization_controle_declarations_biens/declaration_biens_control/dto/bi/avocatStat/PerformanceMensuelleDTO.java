package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.avocatStat;

import java.time.Month;
import java.util.Map;

public  class PerformanceMensuelleDTO {
    private int annee;
    private Map<Month, Long> declarationsParMois;
    private Map<Month, Long> conclusionsParMois;
    private Map<Month, Double> tauxTraitementParMois;

    public PerformanceMensuelleDTO(int annee, Map<Month, Long> declarationsParMois, 
                                  Map<Month, Long> conclusionsParMois, Map<Month, Double> tauxTraitementParMois) {
        this.annee = annee;
        this.declarationsParMois = declarationsParMois;
        this.conclusionsParMois = conclusionsParMois;
        this.tauxTraitementParMois = tauxTraitementParMois;
    }

    // Getters
    public int getAnnee() { return annee; }
    public Map<Month, Long> getDeclarationsParMois() { return declarationsParMois; }
    public Map<Month, Long> getConclusionsParMois() { return conclusionsParMois; }
    public Map<Month, Double> getTauxTraitementParMois() { return tauxTraitementParMois; }
}

