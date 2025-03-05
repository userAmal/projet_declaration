package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeVocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TypeVocabulaireProjection;
import java.util.List;

public interface ITypeVocabulaireData extends JpaRepository<TypeVocabulaire, Long> {
    
    List<TypeVocabulaire> findByIntitule(String intitule);
    
    @Query("SELECT tv.id AS id, tv.intitule AS intitule FROM TypeVocabulaire tv WHERE tv.id = :id")
    List<TypeVocabulaireProjection> getTypeVocabulaireDetails(@Param("id") Long id);
}