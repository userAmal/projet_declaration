package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat;

import java.util.List;


// DTO pour les performances globales
public class PerformanceStatsDTO {
    private List<MonthlyPerformanceDTO> monthlyPerformance;
    private List<YearlyPerformanceDTO> yearlyPerformance;
    private double globalPerformanceRate;
    
    public PerformanceStatsDTO(List<MonthlyPerformanceDTO> monthlyPerformance,
                              List<YearlyPerformanceDTO> yearlyPerformance,
                              double globalPerformanceRate) {
        this.monthlyPerformance = monthlyPerformance;
        this.yearlyPerformance = yearlyPerformance;
        this.globalPerformanceRate = globalPerformanceRate;
    }
    
    // Getters and Setters
    public List<MonthlyPerformanceDTO> getMonthlyPerformance() { return monthlyPerformance; }
    public void setMonthlyPerformance(List<MonthlyPerformanceDTO> monthlyPerformance) { this.monthlyPerformance = monthlyPerformance; }
    
    public List<YearlyPerformanceDTO> getYearlyPerformance() { return yearlyPerformance; }
    public void setYearlyPerformance(List<YearlyPerformanceDTO> yearlyPerformance) { this.yearlyPerformance = yearlyPerformance; }
    
    public double getGlobalPerformanceRate() { return globalPerformanceRate; }
    public void setGlobalPerformanceRate(double globalPerformanceRate) { this.globalPerformanceRate = globalPerformanceRate; }
}
