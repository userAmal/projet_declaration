package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.DeclarationProjection;

import lombok.Data;


@Data
public class DeclarationDto {

    public DeclarationDto() {
    }


    public DeclarationDto(DeclarationProjection declarationProjection) {
        this.id = declarationProjection.getId();
        this.typeDeclaration= declarationProjection.getTypeDeclaration();
        this.dateDeclaration = declarationProjection.getDateDeclaration();
        this.utilisateurId = declarationProjection.getUtilisateurId();
        
    }

    private Long id;
    private LocalDate dateDeclaration;
    private Vocabulaire typeDeclaration;
    private Utilisateur utilisateurId;
}
