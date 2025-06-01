package com.informatization_controle_declarations_biens.declaration_biens_control.controller.bi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.AdvisorPerformance;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.DecisionStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.DeclarationStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.ReportStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.WorkflowStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.bi.PGStatisticsService;

@RestController
@RequestMapping("/api/pg/statistics")
public class PGStatisticsController {
    private final PGStatisticsService pgStatisticsService;

    public PGStatisticsController(PGStatisticsService pgStatisticsService) {
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
}