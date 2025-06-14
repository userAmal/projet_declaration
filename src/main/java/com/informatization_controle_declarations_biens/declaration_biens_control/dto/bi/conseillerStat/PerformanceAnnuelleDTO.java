package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceAnnuelleDTO {
    private Long utilisateurId;
    private String nom;
    private String prenom;
    private RoleEnum role;
    private int annee;
    private long declarationsTraiteesAnnee;
    private long rapportsGeneresAnnee;
    private long observationsAnnee;
    private double tempsMoyenTraitement;
    private double moyenneDeclarationsParMois;
    private double moyenneRapportsParMois;
    private double scoreEfficacite;
    private String niveauPerformance; // EXCELLENT, BON, MOYEN, FAIBLE
}

