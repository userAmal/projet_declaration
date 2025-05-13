package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.DisponibilitesEnBanque;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.DisponibilitesEnBanqueProjection;

import java.util.List;
import java.util.Optional;

public interface IDisponibilitesEnBanqueData extends JpaRepository<DisponibilitesEnBanque, Long> {
    
    @Query("SELECT d FROM DisponibilitesEnBanque d WHERE d.idDeclaration.id = :declarationId")
    List<DisponibilitesEnBanqueProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);
    
    @Query("SELECT d FROM DisponibilitesEnBanque d LEFT JOIN FETCH d.idDeclaration WHERE d.id = :id")
    Optional<DisponibilitesEnBanque> findSimplifiedById(@Param("id") Long id);

    // Dans IDisponibilitesEnBanqueData.java
@Query("SELECT d FROM DisponibilitesEnBanque d WHERE d.banque.id = :banqueId")
List<DisponibilitesEnBanqueProjection> findByBanqueId(@Param("banqueId") Long banqueId);
}
