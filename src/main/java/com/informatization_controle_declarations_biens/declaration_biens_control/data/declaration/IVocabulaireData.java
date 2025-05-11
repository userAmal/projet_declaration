package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VocabulaireProjection;

@Repository
public interface IVocabulaireData extends JpaRepository<Vocabulaire, Long> {
    @Query("SELECT v FROM Vocabulaire v WHERE v.isActive = true")
List<Vocabulaire> findAllActive();

    Optional<Vocabulaire> findByIntitule(String intitule);

    @Query("SELECT v FROM Vocabulaire v WHERE v.typevocabulaire.id = :typeId")
    List<Vocabulaire> findByTypevocabulaireId(@Param("typeId") Long typeId);

    @Query("SELECT v FROM Vocabulaire v WHERE v.vocabulaireParent.id = :parentId")
    List<Vocabulaire> findByVocabulaireParentId(@Param("parentId") Long parentId);

    @Query("SELECT v FROM Vocabulaire v WHERE v.vocabulaireParent IS NULL")
    List<Vocabulaire> findAllRootVocabularies();

    @Query("SELECT v FROM Vocabulaire v WHERE LOWER(v.intitule) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Vocabulaire> searchByIntitule(@Param("keyword") String keyword);
    @Query("SELECT v FROM Vocabulaire v WHERE v.typevocabulaire.id = :typeId AND v.vocabulaireParent IS NULL")
List<Vocabulaire> findRootsByType(@Param("typeId") Long typeId);
@Query("SELECT v.id as id, v.intitule as intitule, t.intitule as typeIntitule, p.intitule as parentIntitule " +
       "FROM Vocabulaire v " +
       "LEFT JOIN v.typevocabulaire t " +
       "LEFT JOIN v.vocabulaireParent p " +
       "WHERE v.id = :id")
List<VocabulaireProjection> getVocabulaireDetails(@Param("id") Long id);
List<Vocabulaire> findByIntituleAndTypevocabulaireId(String intitule, Long typevocabulaireId);
boolean existsByIntituleAndTypevocabulaireIdAndIdNot(String intitule, Long typeVocabulaireId, Long id);



}
