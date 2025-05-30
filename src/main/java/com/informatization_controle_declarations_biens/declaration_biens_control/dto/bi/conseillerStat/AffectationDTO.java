package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AffectationDTO {
    private Long conseillerId;
    private String conseillerNom;
    private LocalDate dateAffectation;
    private LocalDate dateFinAffectation;
}
