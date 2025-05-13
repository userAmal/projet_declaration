package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDisponibilitesEnBanqueData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.DisponibilitesEnBanque;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDisponibilitesEnBanqueService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.DisponibilitesEnBanqueProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DisponibilitesEnBanqueService implements IDisponibilitesEnBanqueService {

    private final IDisponibilitesEnBanqueData data;

    public DisponibilitesEnBanqueService(IDisponibilitesEnBanqueData data) {
        this.data = data;
    }

    @Override
    public List<DisponibilitesEnBanqueProjection> getByDeclaration(Long declarationId) {
        return data.findByIdDeclaration_Id(declarationId);
    }
// Dans DisponibilitesEnBanqueService.java
@Override
public List<DisponibilitesEnBanqueProjection> findByBanque(Long banqueId) {
    return data.findByBanqueId(banqueId);
}
    @Override
    public List<DisponibilitesEnBanque> findAll() {
        return data.findAll();
    }

    @Override
    public Optional<DisponibilitesEnBanque> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return data.findSimplifiedById(id);    }

    @Override
    public DisponibilitesEnBanque save(DisponibilitesEnBanque entity) {
        return data.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        data.deleteById(id);
    }
}
