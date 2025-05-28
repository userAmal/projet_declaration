package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat;

import java.util.Map;

public record TemporalStats(
    String period,
    Map<String, TemporalData> data
) {}
