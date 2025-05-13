package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IRevenusData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Revenus;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IRevenusService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.RevenusProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RevenusService implements IRevenusService {
    @Autowired
    private final IRevenusData Data;


    public RevenusService(IRevenusData Data) {
        this.Data = Data;
    }
// Dans RevenusService.java
@Override
public List<RevenusProjection> findByAutresRevenus(Long autresRevenusId) {
    return Data.findByAutresRevenusId(autresRevenusId);
}
    @Override
    public List<Revenus> findAll() {
        return Data.findAll();
    }

    @Override
    public Optional<Revenus> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Data.findSimplifiedById(id);    }

    @Override
    public Revenus save(Revenus revenus) {
        return Data.save(revenus);
    }

    @Override
    public void deleteById(Long id) {
        Data.deleteById(id);
    }

    @Override
    public List<RevenusProjection> getByDeclaration(Long declarationId) {
        return Data.findByDeclarationId(declarationId);
    }
}
