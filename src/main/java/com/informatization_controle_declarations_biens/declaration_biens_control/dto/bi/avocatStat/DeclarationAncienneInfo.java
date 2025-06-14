package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.avocatStat;

import java.time.LocalDate;

public  class DeclarationAncienneInfo {
    private Long id;
    private String typeDeclaration;
    private LocalDate dateDeclaration;
    private long joursEnAttente;
    private String nomAssujetti;

    public DeclarationAncienneInfo(Long id, String typeDeclaration, LocalDate dateDeclaration, 
                                  long joursEnAttente, String nomAssujetti) {
        this.id = id;
        this.typeDeclaration = typeDeclaration;
        this.dateDeclaration = dateDeclaration;
        this.joursEnAttente = joursEnAttente;
        this.nomAssujetti = nomAssujetti;
    }

    // Getters
    public Long getId() { return id; }
    public String getTypeDeclaration() { return typeDeclaration; }
    public LocalDate getDateDeclaration() { return dateDeclaration; }
    public long getJoursEnAttente() { return joursEnAttente; }
    public String getNomAssujetti() { return nomAssujetti; }
}
