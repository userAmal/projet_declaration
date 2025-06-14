package com.informatization_controle_declarations_biens.declaration_biens_control.controller.bi;


import com.informatization_controle_declarations_biens.declaration_biens_control.service.bi.AvocatGeneralStatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/avocat-general/statistics")
public class AvocatGeneralStatisticsController {

    private final AvocatGeneralStatisticsService statisticsService;

    public AvocatGeneralStatisticsController(AvocatGeneralStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Get global statistics for the Avocat General
     * @param avocatGeneralId ID of the Avocat General
     * @return Global statistics DTO
     */
    @GetMapping("/global/{avocatGeneralId}")
    public AvocatGeneralStatisticsService.AvocatGeneralGlobalStatsDTO getGlobalStats(
            @PathVariable Long avocatGeneralId) {
        return statisticsService.getGlobalStats(avocatGeneralId);
    }

    /**
     * Get conclusion statistics (acceptance/rejection rates)
     * @param avocatGeneralId ID of the Avocat General
     * @return Conclusion statistics DTO
     */
    @GetMapping("/conclusions/{avocatGeneralId}")
    public AvocatGeneralStatisticsService.ConclusionStatsDTO getConclusionStats(
            @PathVariable Long avocatGeneralId) {
        return statisticsService.getConclusionStats(avocatGeneralId);
    }


    /**
     * Get analysis by declaration type
     * @param avocatGeneralId ID of the Avocat General
     * @return List of analysis by type DTOs
     */
    @GetMapping("/analysis/type/{avocatGeneralId}")
    public java.util.List<AvocatGeneralStatisticsService.DeclarationTypeAnalysisDTO> getAnalyseParType(
            @PathVariable Long avocatGeneralId) {
        return statisticsService.getAnalyseParType(avocatGeneralId);
    }


    /**
     * Get current workload metrics
     * @param avocatGeneralId ID of the Avocat General
     * @return Workload DTO
     */
    @GetMapping("/workload/{avocatGeneralId}")
    public AvocatGeneralStatisticsService.ChargeTravailtDTO getChargeTravail(
            @PathVariable Long avocatGeneralId) {
        return statisticsService.getChargeTravail(avocatGeneralId);
    }


    /**
     * Get complete dashboard with all statistics
     * @param avocatGeneralId ID of the Avocat General
     * @return Map containing all statistics
     */
    @GetMapping("/dashboard/{avocatGeneralId}")
    public Map<String, Object> getDashboardComplet(
            @PathVariable Long avocatGeneralId) {
        return statisticsService.getDashboardComplet(avocatGeneralId);
    }
    
}