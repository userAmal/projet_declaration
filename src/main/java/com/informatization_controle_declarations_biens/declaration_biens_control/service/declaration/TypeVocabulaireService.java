package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.ITypeVocabulaireData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeVocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.ITypeVocabulaireService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TypeVocabulaireProjection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TypeVocabulaireService implements ITypeVocabulaireService {

    @Autowired
    private ITypeVocabulaireData typeVocabulaireData;

    @Override
    public List<TypeVocabulaire> findAll() {
        return typeVocabulaireData.findAll();
    }

    @Override
    public Optional<TypeVocabulaire> findById(Long id) {
        return typeVocabulaireData.findById(id);
    }

    @Override
    public TypeVocabulaire save(TypeVocabulaire typeVocabulaire) {
        return typeVocabulaireData.save(typeVocabulaire);
    }

    @Override
    public void deleteById(Long id) {
        typeVocabulaireData.deleteById(id);
    }

    @Override
    public List<TypeVocabulaire> findByIntitule(String intitule) {
        return typeVocabulaireData.findByIntitule(intitule);
    }

    @Override
    public List<TypeVocabulaireProjection> getTypeVocabulaireDetails(Long id) {
        return typeVocabulaireData.getTypeVocabulaireDetails(id);
    }
}