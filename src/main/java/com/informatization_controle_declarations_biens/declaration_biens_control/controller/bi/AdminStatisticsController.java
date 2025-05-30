package com.informatization_controle_declarations_biens.declaration_biens_control.controller.bi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.AssujettiStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.DeclarationStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.UserStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.VocabularyStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.bi.AdminStatisticsService;

import java.util.HashMap;
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
}