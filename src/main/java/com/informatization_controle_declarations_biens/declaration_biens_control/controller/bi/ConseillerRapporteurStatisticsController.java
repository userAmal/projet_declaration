package com.informatization_controle_declarations_biens.declaration_biens_control.controller.bi;


import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.*;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.bi.ConseillerRapporteurStatisticsService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/conseiller-statistics")
@RequiredArgsConstructor
public class ConseillerRapporteurStatisticsController {

    private final ConseillerRapporteurStatisticsService statisticsService;

    // Endpoints principaux
    @GetMapping("/{conseillerId}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardConseiller(
            @PathVariable Long conseillerId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("statistiquesGenerales", statisticsService.getStatistiquesConseiller(conseillerId));
        dashboard.put("declarationsAssignees", statisticsService.consulterDeclarationsAssignees(conseillerId));
        dashboard.put("statistiquesMensuelles", statisticsService.getStatistiquesParMois(conseillerId));
        dashboard.put("performanceVerification", statisticsService.getPerformanceVerification(conseillerId));
        dashboard.put("chargeTravail", statisticsService.getChargeUtilisateur(conseillerId));
        
        return ResponseEntity.ok(dashboard);
    }

    // Endpoints spécifiques
    @GetMapping("/{conseillerId}/declarations-assignees")
    public ResponseEntity<List<DeclarationConseillerDTO>> getDeclarationsAssignees(
            @PathVariable Long conseillerId) {
        return ResponseEntity.ok(statisticsService.consulterDeclarationsAssignees(conseillerId));
    }

    @GetMapping("/{conseillerId}/declarations-anciennes")
    public ResponseEntity<List<DeclarationAncienneDTO>> getDeclarationsAnciennes(
            @PathVariable Long conseillerId) {
        return ResponseEntity.ok(statisticsService.getDeclarationsAnciennesAControler(conseillerId));
    }

    // Endpoints pour les rapports
    @PostMapping("/{conseillerId}/rapports/provisoire/{declarationId}")
    public ResponseEntity<RapportProvisoireStatsDTO> genererRapportProvisoire(
            @PathVariable Long conseillerId,
            @PathVariable Long declarationId) {
        return ResponseEntity.ok(
            statisticsService.genererRapportProvisoire(conseillerId, declarationId)
        );
    }

    // Endpoints pour les vérifications
    @PostMapping("/{conseillerId}/verifications/fraude/{declarationId}")
    public ResponseEntity<VerificationFraudeStatsDTO> verifierDeclaration(
            @PathVariable Long conseillerId,
            @PathVariable Long declarationId) {
        return ResponseEntity.ok(
            statisticsService.verifierDeclaration(conseillerId, declarationId)
        );
    }

    // Endpoints pour les statistiques
    @GetMapping("/{conseillerId}/stats/mensuelles")
    public ResponseEntity<List<StatsMensuellesDTO>> getStatsMensuelles(
            @PathVariable Long conseillerId) {
        return ResponseEntity.ok(statisticsService.getStatistiquesParMois(conseillerId));
    }

    @GetMapping("/{conseillerId}/stats/performance")
    public ResponseEntity<PerformanceVerificationDTO> getPerformance(
            @PathVariable Long conseillerId) {
        return ResponseEntity.ok(statisticsService.getPerformanceVerification(conseillerId));
    }

    @GetMapping("/{conseillerId}/stats/annuelle")
    public ResponseEntity<PerformanceAnnuelleDTO> getPerformanceAnnuelle(
            @PathVariable Long conseillerId) {
        return ResponseEntity.ok(statisticsService.getPerformanceAnnuelleCourante(conseillerId));
    }

    // Endpoints pour la charge de travail
    @GetMapping("/{conseillerId}/charge-travail")
    public ResponseEntity<ChargeUtilisateurDTO> getChargeTravail(
            @PathVariable Long conseillerId) {
        return ResponseEntity.ok(statisticsService.getChargeUtilisateur(conseillerId));
    }
}