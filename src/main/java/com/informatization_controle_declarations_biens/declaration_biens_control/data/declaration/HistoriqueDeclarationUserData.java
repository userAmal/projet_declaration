package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.HistoriqueDeclarationUser;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriqueDeclarationUserData extends JpaRepository<HistoriqueDeclarationUser, Long> {

    List<HistoriqueDeclarationUser> findByDeclarationId(Long declarationId);
    List<HistoriqueDeclarationUser> findByUtilisateurId(Long utilisateurId);
    @Query("SELECT h FROM HistoriqueDeclarationUser h WHERE h.utilisateur.role = :role")
    List<HistoriqueDeclarationUser> findByRole(@Param("role") RoleEnum role);

    // Trouver les affectations actives (sans date de fin)
    @Query("SELECT h FROM HistoriqueDeclarationUser h WHERE h.dateFinAffectation IS NULL")
    List<HistoriqueDeclarationUser> findActiveAffectations();

    // Trouver les affectations actives pour une déclaration spécifique
    @Query("SELECT h FROM HistoriqueDeclarationUser h WHERE h.declaration.id = :declarationId AND h.dateFinAffectation IS NULL")
    List<HistoriqueDeclarationUser> findActiveAffectationsByDeclaration(@Param("declarationId") Long declarationId);

    // Vérifier si un utilisateur a déjà été affecté à une déclaration
    boolean existsByDeclarationIdAndUtilisateurId(Long declarationId, Long utilisateurId);

    // Trouver les historiques dans une période donnée
    @Query("SELECT h FROM HistoriqueDeclarationUser h WHERE h.dateAffectation BETWEEN :startDate AND :endDate")
    List<HistoriqueDeclarationUser> findByPeriod(
            @Param("startDate") LocalDate  startDate,
            @Param("endDate") LocalDate  endDate);

    @Query("SELECT h FROM HistoriqueDeclarationUser h WHERE h.declaration.id = :declarationId AND h.dateFinAffectation IS NULL")
    List<HistoriqueDeclarationUser> findByDeclarationIdAndDateFinAffectationIsNull(@Param("declarationId") Long declarationId);

    @Query("SELECT h.utilisateur FROM HistoriqueDeclarationUser h " +
       "WHERE h.utilisateur.role = :role AND h.declaration.id = :declarationId")
    Optional<Utilisateur> findFirstUtilisateurByRoleAndDeclarationId(
    @Param("role") RoleEnum role,
    @Param("declarationId") Long declarationId);
}