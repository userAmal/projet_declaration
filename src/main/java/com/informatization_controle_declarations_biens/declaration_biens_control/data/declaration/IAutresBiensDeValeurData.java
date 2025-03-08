package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresBiensDeValeur;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresBiensDeValeurProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IAutresBiensDeValeurData extends JpaRepository<AutresBiensDeValeur, Long> {
    
    @Query("SELECT a FROM AutresBiensDeValeur a WHERE a.idDeclaration.id = :declarationId")
    List<AutresBiensDeValeurProjection> findByDeclarationId(@Param("declarationId") Long declarationId);
    
}