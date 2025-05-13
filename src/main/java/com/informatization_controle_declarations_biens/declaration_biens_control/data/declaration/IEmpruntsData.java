package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Emprunts;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EmpruntsProjection;

import java.util.List;
import java.util.Optional;

public interface IEmpruntsData extends JpaRepository<Emprunts, Long> {
    
    @Query("SELECT e FROM Emprunts e WHERE e.idDeclaration.id = :declarationId")
    List<EmpruntsProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);

        @Query("SELECT e FROM Emprunts e LEFT JOIN FETCH e.idDeclaration WHERE e.id = :id")
    Optional<Emprunts> findSimplifiedById(@Param("id") Long id);

    @Query("SELECT e FROM Emprunts e WHERE e.institutionsFinancieres.id = :vocabulaireId")
List<Emprunts> findByInstitutionFinanciere(@Param("vocabulaireId") Long vocabulaireId);

}
