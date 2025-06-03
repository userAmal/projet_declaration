package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWorkloadStatsDto {
    private Long userId;
    private String userFullName;
    private String userEmail;
    private String userRole;
    
    // Charge actuelle
    private Long declarationsEnCours;
    private Long declarationsNouvelles;
    private Long declarationsEnAttente;
    private Long rapportsEnCours;
    
    // Performance mensuelle
    private Long declarationsTraiteesThisMois;
    private Long rapportsFinalisesThisMois;
    private Double tauxReussiteThisMois;
    
    // Performance annuelle
    private Long declarationsTraiteesThisYear;
    private Long rapportsFinalisesThisYear;
    private Double tauxReussiteThisYear;
    
    // Métriques de performance
    private Double tempsMoyenTraitement; // en jours
    private Long scoreCharge;
    private String niveauCharge; // FAIBLE, MODERE, ELEVE, CRITIQUE
    
    // Tendances
    private Double evolutionMensuelle; // pourcentage d'évolution vs mois précédent
    private Double evolutionAnnuelle; // pourcentage d'évolution vs année précédente
}
