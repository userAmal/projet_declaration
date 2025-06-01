package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PGWorkloadDto {
    private Long pgId;
    private String pgNom;
    private String pgEmail;
    private long declarationsEnCours;
    private long declarationsNouvelles;
    private long declarationsNonDeclarees;
    private long declarationsTerminees;
    private long totalCharge;
    private String disponibilite;
    
    // Méthode utilitaire pour l'affichage
    public String getChargeDescription() {
        return String.format("En cours: %d, Nouvelles: %d, Non déclarées: %d (Score: %d - %s)",
                declarationsEnCours, declarationsNouvelles, declarationsNonDeclarees, 
                totalCharge, disponibilite);
    }
}
