package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.informatization_controle_declarations_biens.declaration_biens_control.controller.securite.AuthenticationResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;


public interface IUtilisateurService {
    Utilisateur save(Utilisateur utilisateur);
    
    Utilisateur modifierUtilisateur(Long id, Utilisateur utilisateur);

    void archiverUtilisateur(Long id);
AuthenticationResponse changePassword(String email, String newPassword);

    Optional<Utilisateur> findById(Long id);
    
    Optional<Utilisateur> findByEmail(String email);
    
    List<Utilisateur> findAll();
    
List<Utilisateur> findByRole(@Param("role") RoleEnum role);
    
    List<Utilisateur> searchByFirstnameOrLastname(String keyword);
    
    void deleteById(Long id);
}