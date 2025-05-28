package com.informatization_controle_declarations_biens.declaration_biens_control.controller.bi;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.AdminDashboardStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.AssujettiStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.DeclarationStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.DeclarationTrendDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.PeriodType;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.UserActivityDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.UserStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.VocabularyStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.bi.AdminStatisticsService;

@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {

    private final AdminStatisticsService adminStatsService;

    public AdminStatisticsController(AdminStatisticsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardStatsDTO> getAdminDashboardStats() {
        return ResponseEntity.ok(adminStatsService.getAdminDashboardStatistics());
    }

    @GetMapping("/users")
    public ResponseEntity<UserStatsDTO> getUserStatistics() {
        return ResponseEntity.ok(adminStatsService.getUserStatistics());
    }

    @GetMapping("/user-activity")
    public ResponseEntity<List<UserActivityDTO>> getUserActivityStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminStatsService.getUserActivityStats(startDate, endDate));
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

    @GetMapping("/declaration-trends")
    public ResponseEntity<List<DeclarationTrendDTO>> getDeclarationTrends(
            @RequestParam(defaultValue = "MONTHLY") PeriodType period) {
        return ResponseEntity.ok(adminStatsService.getDeclarationTrends(period));
    }
}
