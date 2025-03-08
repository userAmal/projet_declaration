package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.MeublesMeublants;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.MeublesMeublantsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMeublesMeublantsData extends JpaRepository<MeublesMeublants, Long> {

    @Query("SELECT m FROM MeublesMeublants m WHERE m.idDeclaration.id = :declarationId")
    List<MeublesMeublantsProjection> findByDeclarationId(@Param("declarationId") Long declarationId);
}
