package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.ILesCreancesData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.LesCreances;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.ILesCreancesService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.LesCreancesProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LesCreancesService implements ILesCreancesService {

    @Autowired
    private ILesCreancesData Data;

    @Override
    public List<LesCreances> findAll() {
        return Data.findAll();
    }

    @Override
    public Optional<LesCreances> findById(Long id) {
        return Data.findById(id);
    }

    @Override
    public LesCreances save(LesCreances lesCreances) {
        return Data.save(lesCreances);
    }

    @Override
    public void deleteById(Long id) {
        Data.deleteById(id);
    }

    @Override
    public List<LesCreancesProjection> getByDeclaration(Long declarationId) {
        return Data.findByDeclarationId(declarationId);
    }
}
