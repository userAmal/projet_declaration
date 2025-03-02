package com.informatization_controle_declarations_biens.declaration_biens_control.data.securite;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

@Repository
public interface IUtilisateurData extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);


    
    @Query("SELECT u FROM Utilisateur u WHERE u.statutEmploi = true")
    List<Utilisateur> findAllActiveUsers();

    @Query("SELECT u FROM Utilisateur u WHERE u.role = :role AND u.statutEmploi = true")
    List<Utilisateur> findByRole(@Param("role") RoleEnum role);
    
    

    // Recherche par nom et prénom (ignorer la casse)
    @Query("SELECT u FROM Utilisateur u WHERE LOWER(u.firstname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :keyword, '%')) AND u.statutEmploi = true")
    List<Utilisateur> searchByFirstnameOrLastname(@Param("keyword") String keyword);


}
