package com.informatization_controle_declarations_biens.declaration_biens_control.data.controle;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Amende;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.StatutAmendeEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.controle.AmendeProjection;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AmendeData extends JpaRepository<Amende, Long> {
    
    // Trouver toutes les amendes pour une déclaration spécifique
    List<Amende> findByDeclarationId(Long declarationId);
    
    // Trouver toutes les amendes pour un assujetti spécifique
    @Query("SELECT a FROM Amende a JOIN a.declaration d JOIN d.assujetti asj WHERE asj.id = :assujettiId")
    List<Amende> findByAssujettiId(@Param("assujettiId") Long assujettiId);
    
    // Projection pour obtenir des informations détaillées
    @Query("SELECT a.id as id, d.id as declarationId, asj.nom as nomAssujetti, asj.prenom as prenomAssujetti, " +
           "a.dateAmende as dateAmende, a.montant as montant, a.statut as statut, a.motif as motif " +
           "FROM Amende a JOIN a.declaration d JOIN d.assujetti asj")
    List<AmendeProjection> findAllAmendeProjections();
    
    // Trouver les amendes non payées
    List<Amende> findByStatut(StatutAmendeEnum statut);
    
    // Trouver les amendes pour une période donnée
    List<Amende> findByDateAmendeBetween(LocalDate debut, LocalDate fin);
}