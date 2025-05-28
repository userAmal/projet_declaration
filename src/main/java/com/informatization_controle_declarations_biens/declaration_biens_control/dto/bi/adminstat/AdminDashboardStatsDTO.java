package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDashboardStatsDTO {
    private UserStatsDTO userStats;
    private AssujettiStatsDTO assujettiStats;
    private VocabularyStatsDTO vocabularyStats;
    private DeclarationStatsDTO declarationStats;
    private List<DeclarationTrendDTO> declarationTrends;
}
