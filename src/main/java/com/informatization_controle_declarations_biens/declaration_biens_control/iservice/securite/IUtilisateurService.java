package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite;

import java.util.Optional;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

public interface IUtilisateurService extends IGenericService<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
}
