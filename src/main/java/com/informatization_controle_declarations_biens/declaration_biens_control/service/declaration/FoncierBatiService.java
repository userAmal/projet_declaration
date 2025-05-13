package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IFoncierBatiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IFoncierBatiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierBatiProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoncierBatiService implements IFoncierBatiService {

    private final IFoncierBatiData data;

    public FoncierBatiService(IFoncierBatiData data) {
        this.data = data;
    }

    @Override
    public List<FoncierBatiProjection> getByDeclaration(Long declarationId) {
        return data.findByIdDeclaration_Id(declarationId);
    }

    @Override
    public List<FoncierBati> findAll() {
        return data.findAll();
    }
    @Override
    public Optional<FoncierBati> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return data.findSimplifiedById(id); // Utilisez la méthode simplifiée
    }

    @Override
    public FoncierBati save(FoncierBati entity) {
        return data.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        data.deleteById(id);
    }

    @Override
    public List<FoncierBati> findByNatureId(Long natureId) {
        return data.findByNatureId(natureId);
    }
}
