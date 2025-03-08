package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.VehiculeDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vehicule;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IVehiculeService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VehiculeProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final IVehiculeService service;

    public VehiculeController(IVehiculeService service) {
        this.service = service;
    }

    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<VehiculeDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<VehiculeProjection> projections = service.getByDeclaration(declarationId);
        List<VehiculeDto> dtos = projections.stream()
                .map(VehiculeDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<VehiculeDto>> getAll() {
        List<Vehicule> list = service.findAll();
        List<VehiculeDto> dtos = list.stream()
                .map(VehiculeDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(VehiculeDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VehiculeDto> create(@RequestBody VehiculeDto dto) {
        Vehicule entity = convertToEntity(dto);
        Vehicule saved = service.save(entity);
        return ResponseEntity.ok(new VehiculeDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeDto> update(@PathVariable Long id, @RequestBody VehiculeDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Vehicule entity = convertToEntity(dto);
        entity.setId(id);
        Vehicule updated = service.save(entity);
        return ResponseEntity.ok(new VehiculeDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Vehicule convertToEntity(VehiculeDto dto) {
        Vehicule entity = new Vehicule();
        entity.setId(dto.getId());
        entity.setDesignation(dto.getDesignation());
        entity.setMarque(dto.getMarque());
        entity.setImmatriculation(dto.getImmatriculation());
        entity.setAnneeAcquisition(dto.getAnneeAcquisition());
        entity.setValeurAcquisition(dto.getValeurAcquisition());
        entity.setEtatGeneral(dto.getEtatGeneral());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese());
        entity.setIdDeclaration(dto.getIdDeclaration());
        return entity;
    }
}
