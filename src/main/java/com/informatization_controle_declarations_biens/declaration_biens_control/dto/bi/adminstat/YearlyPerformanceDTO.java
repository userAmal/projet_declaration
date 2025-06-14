package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat;

public class YearlyPerformanceDTO {
    private int year;
    private long totalDeclarations;
    private long finalDeclarations;
    private double performanceRate;
    
    public YearlyPerformanceDTO(int year, long totalDeclarations, long finalDeclarations) {
        this.year = year;
        this.totalDeclarations = totalDeclarations;
        this.finalDeclarations = finalDeclarations;
        this.performanceRate = totalDeclarations > 0 ? (double) finalDeclarations / totalDeclarations * 100 : 0.0;
    }
    
    // Getters and Setters
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public long getTotalDeclarations() { return totalDeclarations; }
    public void setTotalDeclarations(long totalDeclarations) { this.totalDeclarations = totalDeclarations; }
    
    public long getFinalDeclarations() { return finalDeclarations; }
    public void setFinalDeclarations(long finalDeclarations) { this.finalDeclarations = finalDeclarations; }
    
    public double getPerformanceRate() { return performanceRate; }
    public void setPerformanceRate(double performanceRate) { this.performanceRate = performanceRate; }
}
