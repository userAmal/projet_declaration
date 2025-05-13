package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AppareilsElectroMenagers;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AppareilsElectroMenagersProjection;

public interface IAppareilsElectroMenagersData extends JpaRepository<AppareilsElectroMenagers, Long> {
    @Query("SELECT a FROM AppareilsElectroMenagers a WHERE a.idDeclaration.id = :declarationId")
    List<AppareilsElectroMenagersProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);

    
    @Query("SELECT a FROM AppareilsElectroMenagers a LEFT JOIN FETCH a.idDeclaration WHERE a.id = :id")
    Optional<AppareilsElectroMenagers> findSimplifiedById(@Param("id") Long id);

    @Query("SELECT a FROM AppareilsElectroMenagers a WHERE a.designation.intitule LIKE %:designation%")
    List<AppareilsElectroMenagers> findByDesignation(@Param("designation") String designation);
    


}
