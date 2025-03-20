package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IDeclarationData extends JpaRepository<Declaration, Long> {
    
    @Query("SELECT d FROM Declaration d " +
           "LEFT JOIN FETCH d.assujetti " +
           "WHERE d.id = :declarationId")
    Optional<Declaration> getDeclarationWithAssujetti(@Param("declarationId") Long declarationId);
    

    @Query("SELECT DISTINCT d FROM Declaration d " +
           "LEFT JOIN FETCH d.assujetti a " +
           "WHERE d.id = :declarationId")
    Optional<Declaration> getFullDeclarationDetails(@Param("declarationId") Long declarationId);
}