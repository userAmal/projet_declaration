package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vehicule;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VehiculeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IVehiculeData extends JpaRepository<Vehicule, Long> {
    @Query("SELECT v FROM Vehicule v WHERE v.idDeclaration.id = :declarationId")
    List<VehiculeProjection> findByIdDeclaration_Id(@Param("declarationId") Long declarationId);
    
    @Query("SELECT v FROM Vehicule v LEFT JOIN FETCH v.idDeclaration WHERE v.id = :id")
    Optional<Vehicule> findSimplifiedById(@Param("id") Long id);

    @Query("SELECT v FROM Vehicule v WHERE v.designation.id = :designationId")
List<Vehicule> findByDesignationId(@Param("designationId") Long designationId);

}
