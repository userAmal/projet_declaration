package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IMeublesMeublantsData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.MeublesMeublants;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IMeublesMeublantsService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.MeublesMeublantsProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeublesMeublantsService implements IMeublesMeublantsService {
    @Autowired

    private final IMeublesMeublantsData Data;

    public MeublesMeublantsService(IMeublesMeublantsData Data) {
        this.Data = Data;
    }

    @Override
    public List<MeublesMeublants> findAll() {
        return Data.findAll();
    }

    @Override
    public Optional<MeublesMeublants> findById(Long id) {
        return Data.findById(id);
    }

    @Override
    public MeublesMeublants save(MeublesMeublants meublesMeublants) {
        return Data.save(meublesMeublants);
    }

    @Override
    public void deleteById(Long id) {
        Data.deleteById(id);
    }

    @Override
    public List<MeublesMeublantsProjection> getByDeclaration(Long declarationId) {
        return Data.findByDeclarationId(declarationId);
    }
}
