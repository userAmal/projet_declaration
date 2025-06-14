package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat;

public class MonthlyPerformanceDTO {
    private int year;
    private int month;
    private long totalDeclarations;
    private long finalDeclarations;
    private double performanceRate;
    
    public MonthlyPerformanceDTO(int year, int month, long totalDeclarations, long finalDeclarations) {
        this.year = year;
        this.month = month;
        this.totalDeclarations = totalDeclarations;
        this.finalDeclarations = finalDeclarations;
        this.performanceRate = totalDeclarations > 0 ? (double) finalDeclarations / totalDeclarations * 100 : 0.0;
    }
    
    // Getters and Setters
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    
    public long getTotalDeclarations() { return totalDeclarations; }
    public void setTotalDeclarations(long totalDeclarations) { this.totalDeclarations = totalDeclarations; }
    
    public long getFinalDeclarations() { return finalDeclarations; }
    public void setFinalDeclarations(long finalDeclarations) { this.finalDeclarations = finalDeclarations; }
    
    public double getPerformanceRate() { return performanceRate; }
    public void setPerformanceRate(double performanceRate) { this.performanceRate = performanceRate; }
    
    public String getMonthName() {
        String[] months = {"", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                          "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        return months[month];
    }
}
