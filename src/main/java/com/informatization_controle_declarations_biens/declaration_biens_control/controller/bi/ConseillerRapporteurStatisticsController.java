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
    dashboard.put("performance", statisticsService.getPerformanceVerification(conseillerId));

    return ResponseEntity.ok(dashboard);
}
// Ajouter ces méthodes dans votre ConseillerRapporteurStatisticsController

/**
 * Obtenir la charge de travail d'un utilisateur
 */
@GetMapping("/{utilisateurId}/charge-travail")
public ResponseEntity<ChargeUtilisateurDTO> getChargeUtilisateur(
        @PathVariable Long utilisateurId) {
    return ResponseEntity.ok(statisticsService.getChargeUtilisateur(utilisateurId));
}

/**
 * Obtenir la performance annuelle d'un utilisateur pour l'année courante
 */
@GetMapping("/{utilisateurId}/performance-annuelle")
public ResponseEntity<PerformanceAnnuelleDTO> getPerformanceAnnuelleCourante(
        @PathVariable Long utilisateurId) {
    return ResponseEntity.ok(statisticsService.getPerformanceAnnuelleCourante(utilisateurId));
}

/**
 * Obtenir les déclarations les plus anciennes nécessitant un contrôle
 */
@GetMapping("/{utilisateurId}/declarations-anciennes")
public ResponseEntity<List<DeclarationAncienneDTO>> getDeclarationsAnciennesAControler(
        @PathVariable Long utilisateurId) {
    return ResponseEntity.ok(statisticsService.getDeclarationsAnciennesAControler(utilisateurId));
}

/**
 * Dashboard complet avec toutes les nouvelles statistiques
 */
@GetMapping("/{utilisateurId}/dashboard-complet")
public ResponseEntity<Map<String, Object>> getDashboardComplet(@PathVariable Long utilisateurId) {
    Map<String, Object> dashboard = new HashMap<>();
    
    // Statistiques existantes
    dashboard.put("statistiquesGenerales", statisticsService.getStatistiquesConseiller(utilisateurId));
    dashboard.put("declarationsAssignees", statisticsService.consulterDeclarationsAssignees(utilisateurId));
    dashboard.put("statistiquesParMois", statisticsService.getStatistiquesParMois(utilisateurId));
    dashboard.put("performanceVerification", statisticsService.getPerformanceVerification(utilisateurId));
    
    // Nouvelles statistiques
    dashboard.put("chargeTravail", statisticsService.getChargeUtilisateur(utilisateurId));
    dashboard.put("performanceAnnuelle", statisticsService.getPerformanceAnnuelleCourante(utilisateurId));
    dashboard.put("declarationsAnciennes", statisticsService.getDeclarationsAnciennesAControler(utilisateurId));
    
    return ResponseEntity.ok(dashboard);
}

}
