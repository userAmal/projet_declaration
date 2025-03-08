package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Emprunts;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EmpruntsProjection;

import java.util.List;

public interface IEmpruntsData extends JpaRepository<Emprunts, Long> {
    
    @Query("SELECT e FROM Emprunts e WHERE e.idDeclaration.id = :declarationId")
    List<EmpruntsProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);
}
