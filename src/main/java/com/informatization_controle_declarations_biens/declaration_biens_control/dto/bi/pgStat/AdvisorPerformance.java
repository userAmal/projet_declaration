package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat;

public record AdvisorPerformance(
    String nomConseiller,
    long declarationsTraitees,
    long rapportsProduits,
    double tauxAcceptation,
    double tempsMoyenTraitement
) {}