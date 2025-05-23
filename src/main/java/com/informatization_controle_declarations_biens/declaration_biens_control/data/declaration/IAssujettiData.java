package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatAssujettiEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AssujettiProjection;

public interface IAssujettiData extends JpaRepository<Assujetti, Long> {
        
    @Query(value = "from Assujetti a where a.code = :code")
    List<Assujetti> findByCode(@Param("code") String code);
    
    @Query(value = "from Assujetti a where a.nom = :nom")
    List<Assujetti> findByNom(@Param("nom") String nom);
    
    @Query(value = "from Assujetti a where a.email = :email")
    List<Assujetti> findByEmail(@Param("email") String email);
    
    // @Query(value = "from Assujetti a where a.fonction.libelle = :fonctionLibelle")
    // List<Assujetti> findByFonction(@Param("fonctionLibelle") String fonctionLibelle);
    
    // @Query(value = "from Assujetti a where a.institutions.libelle = :institutionLibelle")
    // List<Assujetti> findByInstitution(@Param("institutionLibelle") String institutionLibelle);
    
    @Query("SELECT a FROM Assujetti a WHERE a.etat != :etat")
    List<Assujetti> findAssujettisExcludingEtat(@Param("etat") EtatAssujettiEnum etat);

    
    
    @Query(value = "from Assujetti a where a.datePriseDeService BETWEEN :startDate AND :endDate")
    List<Assujetti> findByDatePriseDeServiceBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT a.id AS id, a.nom AS nom, a.prenom AS prenom, " +
            "a.contacttel AS contact, a.code AS code, a.email AS email, " +
            "civ.libelle AS civiliteLibelle, inst.libelle AS institutionsLibelle, " +
            "admin.libelle AS administrationLibelle, ent.libelle AS entiteLibelle, " +
            "fct.libelle AS fonctionLibelle, a.matricule AS matricule, " +
            "a.datePriseDeService AS datePriseDeService, " +
            "a.dateCessationFonction AS dateCessationFonction, " +
            "a.etat AS etat " +
            "FROM Assujetti a " +
            "LEFT JOIN Vocabulaire civ ON a.civilite = civ.id " +
            "LEFT JOIN Vocabulaire inst ON a.institutions = inst.id " +
            "LEFT JOIN Vocabulaire admin ON a.administration = admin.id " +
            "LEFT JOIN Vocabulaire ent ON a.entite = ent.id " +
            "LEFT JOIN Vocabulaire fct ON a.fonction = fct.id " +
            "WHERE a.id = :id", nativeQuery = true)
    List<AssujettiProjection> getAssujettiDetails(@Param("id") Long id);
    @Query("SELECT a FROM Assujetti a WHERE a.etat = :etat")
        List<Assujetti> findAssujettisWithEtat(@Param("etat") EtatAssujettiEnum etat);


}
