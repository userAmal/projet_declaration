package com.informatization_controle_declarations_biens.declaration_biens_control.controller.bi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.AdvancedStatisticsService;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.AdvisorPerformance;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.DecisionStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.DeclarationStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.ReportStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.TemporalStatsDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.UserWorkloadStatsDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.WorkflowStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.bi.PGStatisticsService;

@RestController
@RequestMapping("/api/pg/statistics")
public class PGStatisticsController {
    private final PGStatisticsService pgStatisticsService;
        private final AdvancedStatisticsService advancedStatisticsService;

    public PGStatisticsController(PGStatisticsService pgStatisticsService ,AdvancedStatisticsService advancedStatisticsService) {
        this.advancedStatisticsService = advancedStatisticsService;
        this.pgStatisticsService = pgStatisticsService;
    }

    @GetMapping("/declarations")
    public ResponseEntity<DeclarationStats> getDeclarationStats() {
        return ResponseEntity.ok(pgStatisticsService.getDeclarationStats());
    }

    @GetMapping("/reports")
    public ResponseEntity<ReportStats> getReportStats() {
        return ResponseEntity.ok(pgStatisticsService.getReportStats());
    }

    @GetMapping("/decisions")
    public ResponseEntity<DecisionStats> getDecisionStats() {
        return ResponseEntity.ok(pgStatisticsService.getDecisionStats());
    }

    @GetMapping("/advisors-performance")
    public ResponseEntity<List<AdvisorPerformance>> getAdvisorsPerformance() {
        return ResponseEntity.ok(pgStatisticsService.getAdvisorsPerformance());
    }



    @GetMapping("/workflow")
    public ResponseEntity<WorkflowStats> getWorkflowStats() {
        return ResponseEntity.ok(pgStatisticsService.getWorkflowStats());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("declarations", pgStatisticsService.getDeclarationStats());
        dashboard.put("reports", pgStatisticsService.getReportStats());
        dashboard.put("decisions", pgStatisticsService.getDecisionStats());
        dashboard.put("workflow", pgStatisticsService.getWorkflowStats());
        dashboard.put("advisorsPerformance", pgStatisticsService.getAdvisorsPerformance());
        
        return ResponseEntity.ok(dashboard);
    }
    

   
    /**
     * Charge de travail d'un utilisateur spécifique
     */
    @GetMapping("/workload/user/{userId}")
    public ResponseEntity<UserWorkloadStatsDto> getUserWorkload(@PathVariable Long userId) {
        UserWorkloadStatsDto workloadStats = advancedStatisticsService.getUserWorkloadStats(userId);
        return ResponseEntity.ok(workloadStats);
    }
    
    /**
     * Statistiques mensuelles pour une année donnée
     */
    @GetMapping("/temporal/monthly")
    public ResponseEntity<List<TemporalStatsDto>> getMonthlyStats(
            @RequestParam(required = false) Integer year) {
        List<TemporalStatsDto> monthlyStats = advancedStatisticsService.getMonthlyStats(year);
        return ResponseEntity.ok(monthlyStats);
    }
    
    /**
     * Statistiques annuelles
     */
    @GetMapping("/temporal/yearly")  
    public ResponseEntity<List<TemporalStatsDto>> getYearlyStats(
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear) {
        List<TemporalStatsDto> yearlyStats = advancedStatisticsService.getYearlyStats(startYear, endYear);
        return ResponseEntity.ok(yearlyStats);
    }
    
   
    /**
     * Dashboard complet avec toutes les statistiques
     */
    @GetMapping("/dashboard/complete")
    public ResponseEntity<Map<String, Object>> getCompleteDashboard(
            @RequestParam(required = false) Integer year) {
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Charge de travail des utilisateurs
        
        // Statistiques temporelles
        dashboard.put("monthlyStats", advancedStatisticsService.getMonthlyStats(year));
        dashboard.put("yearlyStats", advancedStatisticsService.getYearlyStats(null, null));
        
        
        return ResponseEntity.ok(dashboard);
    }
    
    
    
   
  
    
}