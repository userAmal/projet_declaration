package com.informatization_controle_declarations_biens.declaration_biens_control.data.securite;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

@Repository
public interface IUtilisateurData extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);

}
