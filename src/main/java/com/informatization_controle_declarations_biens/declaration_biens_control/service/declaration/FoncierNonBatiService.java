package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IFoncierNonBatiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierNonBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IFoncierNonBatiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierNonBatiProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoncierNonBatiService implements IFoncierNonBatiService {

    @Autowired
    private IFoncierNonBatiData Data;

    @Override
    public List<FoncierNonBati> findAll() {
        return Data.findAll();
    }

    @Override
    public Optional<FoncierNonBati> findById(Long id) {
        return Data.findById(id);
    }

    @Override
    public FoncierNonBati save(FoncierNonBati foncierNonBati) {
        return Data.save(foncierNonBati);
    }

    @Override
    public void deleteById(Long id) {
        Data.deleteById(id);
    }

    @Override
    public List<FoncierNonBatiProjection> getByDeclaration(Long declarationId) {
        return Data.findByDeclarationId(declarationId);
    }
}
