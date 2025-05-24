package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceUtilisateurDTO {
    private String username;
    private String role;
    private long declarationsTraitees;
    private long rapportsGeneres;
    private double tempsTraitementMoyen; // en jours
    private double tauxAcceptation;
}
