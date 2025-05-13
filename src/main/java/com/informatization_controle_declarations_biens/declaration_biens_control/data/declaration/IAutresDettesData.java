package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresDettes;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresDettesProjection;

public interface IAutresDettesData extends JpaRepository<AutresDettes, Long> {
    @Query("SELECT a FROM AutresDettes a WHERE a.idDeclaration.id = :declarationId")
    List<AutresDettesProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);
    
    @Query("SELECT a FROM AutresDettes a LEFT JOIN FETCH a.idDeclaration WHERE a.id = :id")
    Optional<AutresDettes> findSimplifiedById(@Param("id") Long id);
// Dans IAutresDettesData.java
@Query("SELECT a FROM AutresDettes a WHERE a.creanciers.id = :creancierId")
List<AutresDettesProjection> findByCreancierId(@Param("creancierId") Long creancierId);
}