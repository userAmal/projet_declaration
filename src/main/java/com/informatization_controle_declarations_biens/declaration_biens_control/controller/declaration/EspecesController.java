package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.EspecesDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Especes;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IEspecesService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EspecesProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/especes")
public class EspecesController {

    private final IEspecesService service;

    public EspecesController(IEspecesService service) {
        this.service = service;
    }

    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<EspecesDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<EspecesProjection> projections = service.getByDeclaration(declarationId);
        List<EspecesDto> dtos = projections.stream()
                .map(EspecesDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<EspecesDto>> getAll() {
        List<Especes> list = service.findAll();
        List<EspecesDto> dtos = list.stream()
                .map(EspecesDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecesDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(EspecesDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EspecesDto> create(@RequestBody EspecesDto dto) {
        Especes entity = convertToEntity(dto);
        Especes saved = service.save(entity);
        return ResponseEntity.ok(new EspecesDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecesDto> update(@PathVariable Long id, @RequestBody EspecesDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Especes entity = convertToEntity(dto);
        entity.setId(id);
        Especes updated = service.save(entity);
        return ResponseEntity.ok(new EspecesDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Especes convertToEntity(EspecesDto dto) {
        Especes entity = new Especes();
        entity.setId(dto.getId());
        entity.setMonnaie(dto.getMonnaie());
        entity.setDevise(dto.getDevise());
        entity.setTauxChange(dto.getTauxChange());
        entity.setMontantFCFA(dto.getMontantFCFA());
        entity.setMontantTotalFCFA(dto.getMontantTotalFCFA());
        entity.setDateEspece(dto.getDateEspece());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese());
        entity.setIdDeclaration(dto.getIdDeclaration());
        return entity;
    }
}
