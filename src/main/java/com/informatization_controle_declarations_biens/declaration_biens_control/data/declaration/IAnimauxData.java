package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Animaux;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AnimauxProjection;

public interface IAnimauxData extends JpaRepository<Animaux, Long> {
    @Query("SELECT a FROM Animaux a WHERE a.idDeclaration.id = :declarationId")
    List<AnimauxProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);
}

