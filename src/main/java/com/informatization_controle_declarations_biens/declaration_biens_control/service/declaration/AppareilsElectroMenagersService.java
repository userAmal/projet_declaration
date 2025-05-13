package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAppareilsElectroMenagersData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AppareilsElectroMenagers;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAppareilsElectroMenagersService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AppareilsElectroMenagersProjection;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AppareilsElectroMenagersService implements IAppareilsElectroMenagersService {

    private final IAppareilsElectroMenagersData data;

    public AppareilsElectroMenagersService(IAppareilsElectroMenagersData data) {
        this.data = data;
    }
    @Override
    public List<AppareilsElectroMenagers> findByDesignation(String designation) {
        if (designation == null || designation.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return data.findByDesignation(designation);
    }
    @Override
    public List<AppareilsElectroMenagersProjection> getByDeclaration(Long declarationId) {
        return data.findByIdDeclaration_Id(declarationId);
    }

    @Override
    public List<AppareilsElectroMenagers> findAll() {
        return data.findAll();
    }

    @Override
    public Optional<AppareilsElectroMenagers> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return data.findSimplifiedById(id); 
      }

    @Override
    public AppareilsElectroMenagers save(AppareilsElectroMenagers entity) {
        return data.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        data.deleteById(id);
    }
}
