package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RapportDTO {
    private Rapport.Type type;
    private LocalDate dateCreation;
    private Long createurId;
    private String createurNom;
}

