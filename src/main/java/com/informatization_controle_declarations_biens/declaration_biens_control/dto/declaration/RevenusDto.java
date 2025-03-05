package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.RevenusProjection;
import lombok.Data;

@Data
public class RevenusDto {
    private Long id;
    private float salaireMensuelNet;
    private Vocabulaire autresRevenus;
    private LocalDate dateCreation;

    public RevenusDto() {}

    public RevenusDto(RevenusProjection projection) {
        this.id = projection.getId();
        this.salaireMensuelNet = projection.getSalaireMensuelNet();
        this.autresRevenus = projection.getAutresRevenus();
        this.dateCreation = projection.getDateCreation();
    }
}
