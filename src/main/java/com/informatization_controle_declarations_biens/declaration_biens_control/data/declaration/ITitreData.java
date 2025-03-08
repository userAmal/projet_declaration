package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Titres;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TitresProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITitreData extends JpaRepository<Titres, Long> {

    @Query("SELECT t FROM Titres t WHERE t.idDeclaration.id = :declarationId")
    List<TitresProjection> findByDeclarationId(@Param("declarationId") Long declarationId);
}
