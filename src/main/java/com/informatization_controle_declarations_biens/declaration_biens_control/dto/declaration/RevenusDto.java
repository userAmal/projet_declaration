package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.RevenusProjection;
import lombok.Data;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Revenus;

@Data
public class RevenusDto {
    private Long id;
    private float salaireMensuelNet;
    private Vocabulaire autresRevenus;
    private LocalDate dateCreation;
    private Declaration idDeclaration;


    public RevenusDto() {}

    public RevenusDto(RevenusProjection projection) {
        this.id = projection.getId();
        this.salaireMensuelNet = projection.getSalaireMensuelNet();
        this.autresRevenus = projection.getAutresRevenus();
        this.dateCreation = projection.getDateCreation();
        this.idDeclaration = projection.getIdDeclaration();

    }

    public RevenusDto(Revenus revenus) {
        this.id = revenus.getId();
        this.salaireMensuelNet = revenus.getSalaireMensuelNet();
        this.autresRevenus = revenus.getAutresRevenus();
        this.dateCreation = revenus.getDateCreation();
        this.idDeclaration = revenus.getIdDeclaration();

    }
}
