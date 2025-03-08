package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IEspecesData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Especes;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IEspecesService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EspecesProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecesService implements IEspecesService {

    private final IEspecesData data;

    public EspecesService(IEspecesData data) {
        this.data = data;
    }

    @Override
    public List<EspecesProjection> getByDeclaration(Long declarationId) {
        return data.findByIdDeclaration_Id(declarationId);
    }

    @Override
    public List<Especes> findAll() {
        return data.findAll();
    }

    @Override
    public Optional<Especes> findById(Long id) {
        return data.findById(id);
    }

    @Override
    public Especes save(Especes entity) {
        return data.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        data.deleteById(id);
    }
}
