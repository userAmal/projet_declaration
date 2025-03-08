package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierBatiProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IFoncierBatiData extends JpaRepository<FoncierBati, Long> {
    @Query("SELECT f FROM FoncierBati f WHERE f.idDeclaration.id = :declarationId")
    List<FoncierBatiProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);
}
