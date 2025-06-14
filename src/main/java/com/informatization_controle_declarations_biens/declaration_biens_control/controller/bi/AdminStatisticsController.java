package com.informatization_controle_declarations_biens.declaration_biens_control.controller.bi;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.AssujettiStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.DeclarationStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.MonthlyPerformanceDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.PerformanceStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.UserStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.VocabularyStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.YearlyPerformanceDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.bi.AdminStatisticsService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {
    
    private final AdminStatisticsService adminStatsService;
    
    public AdminStatisticsController(AdminStatisticsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }
    
    @GetMapping("/users")
    public ResponseEntity<UserStatsDTO> getUserStatistics() {
        return ResponseEntity.ok(adminStatsService.getUserStatistics());
    }
    
    @GetMapping("/assujettis")
    public ResponseEntity<AssujettiStatsDTO> getAssujettiStatistics() {
        return ResponseEntity.ok(adminStatsService.getAssujettiStatistics());
    }
    
    @GetMapping("/vocabulary")
    public ResponseEntity<VocabularyStatsDTO> getVocabularyStatistics() {
        return ResponseEntity.ok(adminStatsService.getVocabularyStatistics());
    }
    
    @GetMapping("/declarations")
    public ResponseEntity<DeclarationStatsDTO> getDeclarationStatistics() {
        return ResponseEntity.ok(adminStatsService.getDeclarationStatistics());
    }
    

    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("users", adminStatsService.getUserStatistics());
        dashboard.put("assujettis", adminStatsService.getAssujettiStatistics());
        dashboard.put("vocabulary", adminStatsService.getVocabularyStatistics());
        dashboard.put("declarations", adminStatsService.getDeclarationStatistics());
        
        return ResponseEntity.ok(dashboard);
    }

       /**
     * Obtenir les performances mensuelles pour une année donnée
     */
    @GetMapping("/performance/monthly/{year}")
    public ResponseEntity<List<MonthlyPerformanceDTO>> getMonthlyPerformance(@PathVariable int year) {
        try {
            List<MonthlyPerformanceDTO> performance = adminStatsService.getMonthlyPerformance(year);
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtenir les performances annuelles
     */
    @GetMapping("/performance/yearly")
    public ResponseEntity<List<YearlyPerformanceDTO>> getYearlyPerformance() {
        try {
            List<YearlyPerformanceDTO> performance = adminStatsService.getYearlyPerformance();
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    /**
     * Obtenir les statistiques complètes de performance
     */
    @GetMapping("/performance/complete")
    public ResponseEntity<PerformanceStatsDTO> getCompletePerformanceStats(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            PerformanceStatsDTO stats = adminStatsService.getPerformanceStatistics(year, startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtenir les performances de l'année courante
     */
    @GetMapping("/performance/current-year")
    public ResponseEntity<List<MonthlyPerformanceDTO>> getCurrentYearPerformance() {
        try {
            List<MonthlyPerformanceDTO> performance = adminStatsService.getCurrentYearPerformance();
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/performance/dashboard")
    public ResponseEntity<Map<String, Object>> getPerformanceDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // Performances de l'année courante
            dashboard.put("currentYearPerformance", adminStatsService.getCurrentYearPerformance());
            
            // Performances annuelles
            dashboard.put("yearlyPerformance", adminStatsService.getYearlyPerformance());
            
            // Top utilisateurs actuels
            // Calcul de la performance globale
            List<YearlyPerformanceDTO> yearlyPerf = adminStatsService.getYearlyPerformance();
            double globalRate = yearlyPerf.stream()
                .mapToDouble(YearlyPerformanceDTO::getPerformanceRate)
                .average()
                .orElse(0.0);
            dashboard.put("globalPerformanceRate", Math.round(globalRate * 100.0) / 100.0);
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}