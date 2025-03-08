package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.DisponibilitesEnBanque;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.DisponibilitesEnBanqueProjection;

import java.util.List;

public interface IDisponibilitesEnBanqueData extends JpaRepository<DisponibilitesEnBanque, Long> {
    
    @Query("SELECT d FROM DisponibilitesEnBanque d WHERE d.idDeclaration.id = :declarationId")
    List<DisponibilitesEnBanqueProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);
    
}
