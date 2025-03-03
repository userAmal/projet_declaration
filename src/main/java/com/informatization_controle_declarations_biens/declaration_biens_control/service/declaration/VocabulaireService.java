package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IVocabulaireData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IVocabulaireService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VocabulaireProjection;

@Service
@Transactional
public class VocabulaireService implements IVocabulaireService {
    
    @Autowired
    private IVocabulaireData vocabulaireData;

    @Override
    public List<Vocabulaire> findAll() {
        return vocabulaireData.findAll();
    }

    @Override
    public Optional<Vocabulaire> findById(Long id) {
        return vocabulaireData.findById(id);
    }

    @Override
    public Vocabulaire save(Vocabulaire vocabulaire) {
        return vocabulaireData.save(vocabulaire);
    }

    @Override
    public void deleteById(Long id) {
        vocabulaireData.deleteById(id);
    }

    @Override
    public List<Vocabulaire> findByIntitule(String intitule) {
        return vocabulaireData.findByIntitule(intitule).map(List::of).orElse(Collections.emptyList());
    }

    @Override
    public List<Vocabulaire> findByTypeVocabulaire(Long typeVocabulaireId) {
        return vocabulaireData.findByTypevocabulaireId(typeVocabulaireId);
    }

    @Override
    public List<Vocabulaire> findByVocabulaireParent(Long vocabulaireParentId) {
        return vocabulaireData.findByVocabulaireParentId(vocabulaireParentId);
    }

    @Override
    public List<VocabulaireProjection> getVocabulaireDetails(Long id) {
        return vocabulaireData.getVocabulaireDetails(id);
    }
}
