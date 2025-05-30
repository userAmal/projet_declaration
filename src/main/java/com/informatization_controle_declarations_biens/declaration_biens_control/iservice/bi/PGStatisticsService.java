package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.bi;

import java.util.List;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.AdvisorPerformance;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.DecisionStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.DeclarationStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.ReportStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.TemporalStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.WorkflowStats;

public interface PGStatisticsService {
    // Statistiques sur les déclarations
    DeclarationStats getDeclarationStats();
    
    // Statistiques sur les rapports
    ReportStats getReportStats();
    
    // Statistiques sur les décisions
    DecisionStats getDecisionStats();
    
    // Statistiques sur les performances des conseillers
    List<AdvisorPerformance> getAdvisorsPerformance();

    
    // Statistiques sur le workflow
    WorkflowStats getWorkflowStats();
}
