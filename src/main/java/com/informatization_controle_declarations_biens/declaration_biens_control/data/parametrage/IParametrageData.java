package com.informatization_controle_declarations_biens.declaration_biens_control.data.parametrage;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.parametrage.Parametrage;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.parametrage.ParametrageProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IParametrageData extends JpaRepository<Parametrage, Long> {

    Optional<Parametrage> findByCode(String code);

    @Query("SELECT p FROM Parametrage p WHERE p.id = :id")
    Optional<ParametrageProjection> findProjectedById(@Param("id") Long id);

}
    
