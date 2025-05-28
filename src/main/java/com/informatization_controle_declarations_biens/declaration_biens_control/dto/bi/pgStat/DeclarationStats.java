package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat;

public class DeclarationStats {
    private long total;
    private long initiales;
    private long misesAJour;

    // Add this constructor
    public DeclarationStats(long total, long initiales, long misesAJour) {
        this.total = total;
        this.initiales = initiales;
        this.misesAJour = misesAJour;
    }

    // Getters
    public long getTotal() {
        return total;
    }

    public long getInitiales() {
        return initiales;
    }

    public long getMisesAJour() {
        return misesAJour;
    }
}