package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IVehiculeData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vehicule;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IVehiculeService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VehiculeProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculeService implements IVehiculeService {

    private final IVehiculeData data;

    public VehiculeService(IVehiculeData data) {
        this.data = data;
    }

    @Override
    public List<VehiculeProjection> getByDeclaration(Long declarationId) {
        return data.findByIdDeclaration_Id(declarationId);
    }

    @Override
    public List<Vehicule> findAll() {
        return data.findAll();
    }

    @Override
    public Optional<Vehicule> findById(Long id) {
        return data.findById(id);
    }

    @Override
    public Vehicule save(Vehicule entity) {
        return data.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        data.deleteById(id);
    }
}
