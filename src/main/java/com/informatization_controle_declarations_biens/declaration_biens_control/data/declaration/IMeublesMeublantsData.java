package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.MeublesMeublants;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.MeublesMeublantsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMeublesMeublantsData extends JpaRepository<MeublesMeublants, Long> {

    @Query("SELECT m FROM MeublesMeublants m WHERE m.idDeclaration.id = :declarationId")
    List<MeublesMeublantsProjection> findByDeclarationId(@Param("declarationId") Long declarationId);

    
    @Query("SELECT m FROM MeublesMeublants m LEFT JOIN FETCH m.idDeclaration WHERE m.id = :id")
    Optional<MeublesMeublants> findSimplifiedById(@Param("id") Long id);

    @Query("SELECT m FROM MeublesMeublants m WHERE LOWER(m.designation.intitule) LIKE LOWER(CONCAT('%', :designation, '%'))")
List<MeublesMeublants> findByDesignationContainingIgnoreCase(@Param("designation") String designation);

}
