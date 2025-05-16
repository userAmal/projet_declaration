package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.HistoriqueDeclarationUser;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IHistoriqueDeclarationUserService {

    HistoriqueDeclarationUser createHistorique(Long declarationId, Long utilisateurId);

    // Clôturer une affectation
    HistoriqueDeclarationUser closeAffectation(Long historiqueId);

    // Récupérer l'historique d'une déclaration
    List<HistoriqueDeclarationUser> getHistoriqueByDeclaration(Long declarationId);

    // Récupérer l'historique d'un utilisateur
    List<HistoriqueDeclarationUser> getHistoriqueByUtilisateur(Long utilisateurId);

    // Récupérer les affectations par rôle
    List<HistoriqueDeclarationUser> getHistoriqueByRole(RoleEnum role);

    // Récupérer les affectations actives
    List<HistoriqueDeclarationUser> getActiveAffectations();

    // Récupérer les affectations actives pour une déclaration
    List<HistoriqueDeclarationUser> getActiveAffectationsByDeclaration(Long declarationId);

    // Vérifier si une affectation existe
    boolean existsAffectation(Long declarationId, Long utilisateurId);

    // Récupérer les affectations dans une période
    List<HistoriqueDeclarationUser> getHistoriqueByPeriod(LocalDate startDate, LocalDate endDate);
    Optional<Utilisateur> getFirstUtilisateurByRoleAndDeclaration(RoleEnum role, Long declarationId);
}