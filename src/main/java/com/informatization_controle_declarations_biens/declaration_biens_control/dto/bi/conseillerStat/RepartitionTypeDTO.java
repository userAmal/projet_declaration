package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepartitionTypeDTO {
    private TypeDeclarationEnum type;
    private long nombre;
    private double pourcentage;
}
