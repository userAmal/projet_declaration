package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresBiensDeValeur;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresBiensDeValeurProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAutresBiensDeValeurData extends JpaRepository<AutresBiensDeValeur, Long> {
    
    @Query("SELECT a FROM AutresBiensDeValeur a WHERE a.idDeclaration.id = :declarationId")
    List<AutresBiensDeValeurProjection> findByDeclarationId(@Param("declarationId") Long declarationId);
    
    @Query("SELECT a FROM AutresBiensDeValeur a LEFT JOIN FETCH a.idDeclaration WHERE a.id = :id")
    Optional<AutresBiensDeValeur> findSimplifiedById(@Param("id") Long id);

    // Dans IAutresBiensDeValeurData.java
@Query("SELECT a FROM AutresBiensDeValeur a WHERE a.designation.id = :designationId")
List<AutresBiensDeValeurProjection> findByDesignationId(@Param("designationId") Long designationId);
}