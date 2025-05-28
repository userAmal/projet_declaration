package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat;

public record WorkflowStats(
    long nouveauxDossiers,
    long dossiersEnCours,
    long dossiersTermines
) {}
