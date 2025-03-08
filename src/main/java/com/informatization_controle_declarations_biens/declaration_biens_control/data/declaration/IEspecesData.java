package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Especes;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EspecesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IEspecesData extends JpaRepository<Especes, Long> {
    @Query("SELECT e FROM Especes e WHERE e.idDeclaration.id = :declarationId")
    List<EspecesProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);
}
