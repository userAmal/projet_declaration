package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;

import java.util.List;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VocabulaireProjection;

public interface IVocabulaireService extends IGenericService<Vocabulaire, Long> {
    
    List<Vocabulaire> findByIntitule(String intitule);
    
    List<Vocabulaire> findByTypeVocabulaire(Long typeVocabulaireId);
    
    List<Vocabulaire> findByVocabulaireParent(Long vocabulaireParentId);
    
    List<VocabulaireProjection> getVocabulaireDetails(Long id);

    List<Vocabulaire> findByTypeVocabulaireId(Long typeId);
    boolean existsByIntituleAndTypeVocabulaire(String intitule, Long typeVocabulaireId);
    boolean existsByIntituleAndTypeVocabulaireAndIdNot(String intitule, Long typeVocabulaireId, Long id);



}
