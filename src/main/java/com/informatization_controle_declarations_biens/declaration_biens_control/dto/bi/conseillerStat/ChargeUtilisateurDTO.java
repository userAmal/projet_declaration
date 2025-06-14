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
public class ChargeUtilisateurDTO {
    private Long utilisateurId;
    private String nom;
    private String prenom;
    private RoleEnum role;
    private long declarationsEnCours;
    private long declarationsTraiteesMois;
    private double tempsMoyenTraitement;
    private long rapportsProvMois;
    private long observationsMois;
    private double scoreCharge;
    private String statut; // SURCHARGE, CHARGE_ELEVEE, CHARGE_NORMALE, SOUS_CHARGE
}
