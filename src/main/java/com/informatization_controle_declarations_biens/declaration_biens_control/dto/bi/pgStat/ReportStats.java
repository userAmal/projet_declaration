package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat;

public record ReportStats(
    long rapportsProvisoires,
    long rapportsDefinitifs,
    long rapportsEnRetard
) {}
