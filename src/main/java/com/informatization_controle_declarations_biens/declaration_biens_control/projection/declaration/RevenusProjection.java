package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;

public interface RevenusProjection {
    Long getId();
    float getSalaireMensuelNet();
    Vocabulaire getAutresRevenus();
    LocalDate getDateCreation();
}
