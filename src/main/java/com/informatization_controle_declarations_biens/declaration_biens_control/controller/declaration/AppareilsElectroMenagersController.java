package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.AppareilsElectroMenagersDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AppareilsElectroMenagers;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAppareilsElectroMenagersService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AppareilsElectroMenagersProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appareils-electromenagers")
public class AppareilsElectroMenagersController {

    private final IAppareilsElectroMenagersService service;

    public AppareilsElectroMenagersController(IAppareilsElectroMenagersService service) {
        this.service = service;
    }

    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<AppareilsElectroMenagersDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<AppareilsElectroMenagersProjection> projections = service.getByDeclaration(declarationId);
        List<AppareilsElectroMenagersDto> dtos = projections.stream()
                .map(AppareilsElectroMenagersDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<AppareilsElectroMenagersDto>> getAll() {
        List<AppareilsElectroMenagers> list = service.findAll();
        List<AppareilsElectroMenagersDto> dtos = list.stream()
                .map(AppareilsElectroMenagersDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppareilsElectroMenagersDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(AppareilsElectroMenagersDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AppareilsElectroMenagersDto> create(@RequestBody AppareilsElectroMenagersDto dto) {
        AppareilsElectroMenagers entity = convertToEntity(dto);
        AppareilsElectroMenagers saved = service.save(entity);
        return ResponseEntity.ok(new AppareilsElectroMenagersDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppareilsElectroMenagersDto> update(@PathVariable Long id, @RequestBody AppareilsElectroMenagersDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        AppareilsElectroMenagers entity = convertToEntity(dto);
        entity.setId(id);
        AppareilsElectroMenagers updated = service.save(entity);
        return ResponseEntity.ok(new AppareilsElectroMenagersDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private AppareilsElectroMenagers convertToEntity(AppareilsElectroMenagersDto dto) {
        AppareilsElectroMenagers entity = new AppareilsElectroMenagers();
        entity.setId(dto.getId());
        entity.setDesignation(dto.getDesignation());
        entity.setAnneeAcquisition(dto.getAnneeAcquisition());
        entity.setValeurAcquisition(dto.getValeurAcquisition());
        entity.setEtatGeneral(dto.getEtatGeneral());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese());
        entity.setIdDeclaration(dto.getIdDeclaration());
        return entity;
    }
}