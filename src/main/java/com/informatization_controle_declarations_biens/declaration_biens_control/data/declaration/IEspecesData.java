package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Especes;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EspecesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IEspecesData extends JpaRepository<Especes, Long> {
    @Query("SELECT e FROM Especes e WHERE e.idDeclaration.id = :declarationId")
    List<EspecesProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);

        @Query("SELECT e FROM Especes e LEFT JOIN FETCH e.idDeclaration WHERE e.id = :id")
    Optional<Especes> findSimplifiedById(@Param("id") Long id);

    // Dans IEspecesData.java
@Query("SELECT e FROM Especes e WHERE e.monnaie = :monnaie")
List<EspecesProjection> findByMonnaie(@Param("monnaie") Float monnaie);
}
