package com.informatization_controle_declarations_biens.declaration_biens_control.projection.controle;


import java.math.BigDecimal;
import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.StatutAmendeEnum;

public interface AmendeProjection {
    Long getId();
    Long getDeclarationId();
    String getNomAssujetti();
    String getPrenomAssujetti();
    LocalDate getDateAmende();
    BigDecimal getMontant();
    StatutAmendeEnum getStatut();
    String getMotif();
}