package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;


import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.LesCreances;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.LesCreancesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILesCreancesData extends JpaRepository<LesCreances, Long> {

    @Query("SELECT l FROM LesCreances l WHERE l.idDeclaration.id = :declarationId")
    List<LesCreancesProjection> findByDeclarationId(@Param("declarationId") Long declarationId);

    
    @Query("SELECT l FROM LesCreances l LEFT JOIN FETCH l.idDeclaration WHERE l.id = :id")
    Optional<LesCreances> findSimplifiedById(@Param("id") Long id);

    // Dans ILesCreancesData.java
@Query("SELECT l FROM LesCreances l WHERE l.debiteurs.id = :debiteurId")
List<LesCreancesProjection> findByDebiteurId(@Param("debiteurId") Long debiteurId);
}

