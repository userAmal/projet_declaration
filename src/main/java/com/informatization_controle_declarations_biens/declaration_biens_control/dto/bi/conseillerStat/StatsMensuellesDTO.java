package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsMensuellesDTO {
    private String mois;
    private int annee;
    private long declarationsRecues;
    private long declarationsTraitees;
}