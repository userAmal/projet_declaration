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

    @GetMapping("/{conseillerId}")
    public ResponseEntity<ConseillerStatisticsDTO> getStatistiquesConseiller(
            @PathVariable Long conseillerId) {
        return ResponseEntity.ok(statisticsService.getStatistiquesConseiller(conseillerId));
    }

    @GetMapping("/{conseillerId}/declarations")
    public ResponseEntity<List<DeclarationConseillerDTO>> consulterDeclarationsAssignees(
            @PathVariable Long conseillerId) {
        return ResponseEntity.ok(statisticsService.consulterDeclarationsAssignees(conseillerId));
    }

    @PostMapping("/{conseillerId}/rapport-provisoire/{declarationId}")
    public ResponseEntity<RapportProvisoireStatsDTO> genererRapportProvisoire(
            @PathVariable Long conseillerId,
            @PathVariable Long declarationId) {
        return ResponseEntity.ok(statisticsService.genererRapportProvisoire(conseillerId, declarationId));
    }

    @PostMapping("/{conseillerId}/verifier-fraude/{declarationId}")
    public ResponseEntity<VerificationFraudeStatsDTO> verifierDeclaration(
            @PathVariable Long conseillerId,
            @PathVariable Long declarationId) {
        return ResponseEntity.ok(statisticsService.verifierDeclaration(conseillerId, declarationId));
    }

    @GetMapping("/{conseillerId}/stats-mensuelles")
    public ResponseEntity<List<StatsMensuellesDTO>> getStatistiquesMensuelles(
            @PathVariable Long conseillerId) {
        return ResponseEntity.ok(statisticsService.getStatistiquesParMois(conseillerId));
    }

    @GetMapping("/{conseillerId}/repartition-etat")
    public ResponseEntity<List<RepartitionEtatDTO>> getRepartitionParEtat(
            @PathVariable Long conseillerId) {
        return ResponseEntity.ok(statisticsService.getRepartitionDeclarationsParEtat(conseillerId));
    }

    @GetMapping("/{conseillerId}/performance")
    public ResponseEntity<PerformanceVerificationDTO> getPerformanceVerification(
            @PathVariable Long conseillerId) {
        return ResponseEntity.ok(statisticsService.getPerformanceVerification(conseillerId));
    }
    @GetMapping("/{conseillerId}/dashboard")
public ResponseEntity<Map<String, Object>> getDashboardConseiller(@PathVariable Long conseillerId) {
    Map<String, Object> dashboard = new HashMap<>();

    dashboard.put("nombreDeclarationsTraitees", statisticsService.getNombreDeclarationsTraitees(conseillerId));
    dashboard.put("nombreDeclarationsEnCours", statisticsService.getNombreDeclarationsEnCours(conseillerId));
    dashboard.put("nombreDeclarationsAssignees", statisticsService.getNombreDeclarationsAssignees(conseillerId));
    dashboard.put("tempsTraitementMoyen", statisticsService.getTempsTraitementMoyen(conseillerId));
    dashboard.put("statistiquesParMois", statisticsService.getStatistiquesParMois(conseillerId));
    dashboard.put("repartitionParEtat", statisticsService.getRepartitionDeclarationsParEtat(conseillerId));
    dashboard.put("performance", statisticsService.getPerformanceVerification(conseillerId));

    return ResponseEntity.ok(dashboard);
}
// Dans le Controller
@GetMapping("/all")
public ResponseEntity<List<ConseillerGlobalStatsDTO>> getStatistiquesTousConseillers() {
    return ResponseEntity.ok(statisticsService.getStatistiquesTousConseillers());
}

@GetMapping("/{conseillerId}/repartition-type")
public ResponseEntity<List<RepartitionTypeDTO>> getRepartitionParType(
        @PathVariable Long conseillerId) {
    return ResponseEntity.ok(statisticsService.getRepartitionParType(conseillerId));
}


@GetMapping("/{conseillerId}/stats-validation")
public ResponseEntity<ValidationStatsDTO> getStatistiquesValidation(
        @PathVariable Long conseillerId) {
    return ResponseEntity.ok(statisticsService.getStatistiquesValidation(conseillerId));
}
}
