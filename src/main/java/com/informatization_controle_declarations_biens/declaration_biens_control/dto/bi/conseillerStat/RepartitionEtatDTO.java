package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepartitionEtatDTO {
    private EtatDeclarationEnum etat;
    private long nombre;
    private double pourcentage;
}
