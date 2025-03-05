package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import java.util.List;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeVocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TypeVocabulaireProjection;

public interface ITypeVocabulaireService extends IGenericService<TypeVocabulaire, Long> {
    List<TypeVocabulaire> findByIntitule(String intitule);
    List<TypeVocabulaireProjection> getTypeVocabulaireDetails(Long id);
}