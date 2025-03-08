package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresDettes;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresDettesProjection;

public interface IAutresDettesData extends JpaRepository<AutresDettes, Long> {
    @Query("SELECT a FROM AutresDettes a WHERE a.idDeclaration.id = :declarationId")
    List<AutresDettesProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);
}