package com.informatization_controle_declarations_biens.declaration_biens_control.dto.controle;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.StatutAmendeEnum;

@Data
public class AmendeDTO {
    private Long id;
    private Long declarationId;
    private String nomAssujetti;
    private LocalDate dateAmende;
    private BigDecimal montant;
    private StatutAmendeEnum statut;
    private String motif;
    private LocalDate datePaiement;
    private String referencePaiement;
}
