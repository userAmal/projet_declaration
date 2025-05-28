package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat;

import java.util.Map;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatAssujettiEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssujettiStatsDTO {
    private long totalAssujettis;
    private long activeAssujettis;
    private long archivedAssujettis;
    private Map<EtatAssujettiEnum, Long> byEtat;
    private Map<Integer, Long> byYearOfService;
}
