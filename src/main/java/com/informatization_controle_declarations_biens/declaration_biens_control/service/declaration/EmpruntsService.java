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
    public List<Emprunts> getByInstitutionFinanciere(Long vocabulaireId) {
        return data.findByInstitutionFinanciere(vocabulaireId);
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
        if (id == null) {
            return Optional.empty();
        }
        return data.findSimplifiedById(id);    }

    @Override
    public Emprunts save(Emprunts entity) {
        return data.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        data.deleteById(id);
    }
}
