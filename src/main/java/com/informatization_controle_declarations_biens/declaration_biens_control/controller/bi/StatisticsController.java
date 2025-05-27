package com.informatization_controle_declarations_biens.declaration_biens_control.controller.bi;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.*;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.bi.StatisticsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/total-declarations")
    public ResponseEntity<Long> getTotalDeclarations() {
        return ResponseEntity.ok(statisticsService.getTotalDeclarations());
    }

    @GetMapping("/reports-by-type")
    public ResponseEntity<RaportsByTypeDTO> getReportsByType() {
        return ResponseEntity.ok(statisticsService.getReportsByType());
    }

    @GetMapping("/decisions")
    public ResponseEntity<DecisionStatsDTO> getDecisionStats() {
        return ResponseEntity.ok(statisticsService.getDecisionStats());
    }

    @GetMapping("/declarations-by-actor")
    public ResponseEntity<DeclarationsByActorDTO> getDeclarationsByActor() {
        return ResponseEntity.ok(statisticsService.getDeclarationsByActor());
    }

    @GetMapping("/declarations-trend")
    public ResponseEntity<DeclarationsTrendDTO> getDeclarationsTrend(
            @RequestParam(value = "period", defaultValue = "monthly") String period) {
        return ResponseEntity.ok(statisticsService.getDeclarationsTrend(period));
    }

    @GetMapping("/stats-by-user")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATEUR', 'ROLE_PROCUREUR_GENERAL')")
    public ResponseEntity<List<StatsByUserDTO>> getStatsByUser() {
        return ResponseEntity.ok(statisticsService.getStatsByUser());
    }

    @GetMapping("/stats-by-declaration-type")
    public ResponseEntity<List<StatsByDeclarationTypeDTO>> getStatsByDeclarationType() {
        return ResponseEntity.ok(statisticsService.getStatsByDeclarationType());
    }

    // Nouvelles méthodes ajoutées

    @GetMapping("/amende-stats")
    public ResponseEntity<AmendeStatsDTO> getAmendeStats() {
        return ResponseEntity.ok(statisticsService.getAmendeStats());
    }

    @GetMapping("/stats-by-etat")
    public ResponseEntity<List<StatsByEtatDTO>> getStatsByEtat() {
        return ResponseEntity.ok(statisticsService.getStatsByEtat());
    }

    @GetMapping("/stats-by-period")
    public ResponseEntity<List<StatsPeriodeDto>> getStatsByPeriode(
            @RequestParam(value = "period", defaultValue = "monthly") String period) {
        return ResponseEntity.ok(statisticsService.getStatsByPeriode(period));
    }

    @GetMapping("/user-performance")
    public ResponseEntity<List<PerformanceUtilisateurDTO>> getPerformanceUtilisateurs() {
        return ResponseEntity.ok(statisticsService.getPerformanceUtilisateurs());
    }

    @GetMapping("/workflow-stats")
    public ResponseEntity<WorkflowStatsDTO> getWorkflowStats() {
        return ResponseEntity.ok(statisticsService.getWorkflowStats());
    }

    @GetMapping("/admin-stats")
    public ResponseEntity<Map<String, Object>> getStatsForAdmin() {
        return ResponseEntity.ok(statisticsService.getStatsForAdmin());
    }

    @GetMapping("/procureur-stats")
    public ResponseEntity<Map<String, Object>> getStatsForProcureurGeneral() {
        return ResponseEntity.ok(statisticsService.getStatsForProcureurGeneral());
    }

    @GetMapping("/conseiller-stats/{utilisateurId}")
    public ResponseEntity<Map<String, Object>> getStatsForConseillerRapporteur(
            @PathVariable Long utilisateurId) {
        return ResponseEntity.ok(statisticsService.getStatsForConseillerRapporteur(utilisateurId));
    }

    @GetMapping("/avocat-stats/{utilisateurId}")
    public ResponseEntity<Map<String, Object>> getStatsForAvocatGeneral(
            @PathVariable Long utilisateurId) {
        return ResponseEntity.ok(statisticsService.getStatsForAvocatGeneral(utilisateurId));
    }
}