package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Titres;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.ITitresService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TitresProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.ITitreData;
import java.util.List;
import java.util.Optional;

@Service
public class TitresService implements ITitresService {
    @Autowired
    private final ITitreData Data;

    public TitresService(ITitreData Data) {
        this.Data = Data;
    }

    @Override
    public List<Titres> findAll() {
        return Data.findAll();
    }

    @Override
    public Optional<Titres> findById(Long id) {
        return Data.findById(id);
    }

    @Override
    public Titres save(Titres titres) {
        return Data.save(titres);
    }

    @Override
    public void deleteById(Long id) {
        Data.deleteById(id);
    }

    @Override
    public List<TitresProjection> getByDeclaration(Long declarationId) {
        return Data.findByDeclarationId(declarationId);
    }
}
