package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;


import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Revenus;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.RevenusProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRevenusData extends JpaRepository<Revenus, Long> {

    @Query("SELECT r FROM Revenus r WHERE r.idDeclaration.id = :declarationId")
    List<RevenusProjection> findByDeclarationId(@Param("declarationId") Long declarationId);

    
    @Query("SELECT r FROM Revenus r LEFT JOIN FETCH r.idDeclaration WHERE r.id = :id")
    Optional<Revenus> findSimplifiedById(@Param("id") Long id);

    // Dans IRevenusData.java
@Query("SELECT r FROM Revenus r WHERE r.autresRevenus.id = :autresRevenusId")
List<RevenusProjection> findByAutresRevenusId(@Param("autresRevenusId") Long autresRevenusId);
}

