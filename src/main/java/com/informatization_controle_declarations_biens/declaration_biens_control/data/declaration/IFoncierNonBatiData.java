package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;


import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierNonBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierNonBatiProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFoncierNonBatiData extends JpaRepository<FoncierNonBati, Long> {

    @Query("SELECT f FROM FoncierNonBati f WHERE f.idDeclaration.id = :declarationId")
    List<FoncierNonBatiProjection> findByDeclarationId(@Param("declarationId") Long declarationId);

        @Query("SELECT f FROM FoncierNonBati f LEFT JOIN FETCH f.idDeclaration WHERE f.id = :id")
    Optional<FoncierNonBati> findSimplifiedById(@Param("id") Long id);

        @Query("SELECT f FROM FoncierNonBati f WHERE f.nature.id = :natureId")
    List<FoncierNonBati> findByNatureId(@Param("natureId") Long natureId);
}
