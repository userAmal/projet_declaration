package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

public interface DeclarationProjection {
    Long getId();
    LocalDate getDateDeclaration();
    Vocabulaire getTypeDeclaration();
    Utilisateur getUtilisateurId();
}
