package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IEmpruntsData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Emprunts;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IEmpruntsService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EmpruntsProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpruntsService implements IEmpruntsService {

    private final IEmpruntsData data;

    public EmpruntsService(IEmpruntsData data) {
        this.data = data;
    }

    @Override
    public List<EmpruntsProjection> getByDeclaration(Long declarationId) {
        return data.findByIdDeclaration_Id(declarationId);
    }

    @Override
    public List<Emprunts> findAll() {
        return data.findAll();
    }

    @Override
    public Optional<Emprunts> findById(Long id) {
        return data.findById(id);
    }

    @Override
    public Emprunts save(Emprunts entity) {
        return data.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        data.deleteById(id);
    }
}
