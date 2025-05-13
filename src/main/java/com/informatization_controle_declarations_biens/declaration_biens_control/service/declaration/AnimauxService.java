package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAnimauxData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Animaux;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAnimauxService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AnimauxProjection;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AnimauxService implements IAnimauxService {
    @Autowired
    private final IAnimauxData animauxData;

    
    public AnimauxService(IAnimauxData animauxData) {
        this.animauxData = animauxData;
    }
    @Override
    public List<Animaux> findByEspeces(String especes) {
        return animauxData.findByEspecesContainingIgnoreCase(especes);
    }
    
    @Override
    public List<AnimauxProjection> getAnimauxByDeclaration(Long declarationId) {
        return animauxData.findByIdDeclaration_Id(declarationId);
    }

    @Override
    public List<Animaux> findAll() {
        return animauxData.findAll();
    }

    @Override
    public Optional<Animaux> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return animauxData.findSimplifiedById(id);
    }

    @Override
    public Animaux save(Animaux entity) {
        return animauxData.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        animauxData.deleteById(id);
    }
}