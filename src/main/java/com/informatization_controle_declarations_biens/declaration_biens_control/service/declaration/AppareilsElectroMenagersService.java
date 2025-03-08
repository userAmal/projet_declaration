package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAppareilsElectroMenagersData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AppareilsElectroMenagers;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAppareilsElectroMenagersService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AppareilsElectroMenagersProjection;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AppareilsElectroMenagersService implements IAppareilsElectroMenagersService {

    private final IAppareilsElectroMenagersData data;

    public AppareilsElectroMenagersService(IAppareilsElectroMenagersData data) {
        this.data = data;
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
        return data.findById(id);
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
