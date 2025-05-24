package com.informatization_controle_declarations_biens.declaration_biens_control.controller.bi;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.DecisionStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.DeclarationsByActorDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.DeclarationsTrendDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.RaportsByTypeDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.StatsByDeclarationTypeDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.StatsByUserDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.bi.StatisticsService;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/total-declarations")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATEUR', 'ROLE_PROCUREUR_GENERAL')")
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
    
}