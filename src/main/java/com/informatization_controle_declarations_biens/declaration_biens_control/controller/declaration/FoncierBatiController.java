package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.FoncierBatiDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IFoncierBatiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierBatiProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/foncier-bati")
public class FoncierBatiController {

    private final IFoncierBatiService service;

    public FoncierBatiController(IFoncierBatiService service) {
        this.service = service;
    }

    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<FoncierBatiDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<FoncierBatiProjection> projections = service.getByDeclaration(declarationId);
        List<FoncierBatiDto> dtos = projections.stream()
                .map(FoncierBatiDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<FoncierBatiDto>> getAll() {
        List<FoncierBati> list = service.findAll();
        List<FoncierBatiDto> dtos = list.stream()
                .map(FoncierBatiDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoncierBatiDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(FoncierBatiDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FoncierBatiDto> create(@RequestBody FoncierBatiDto dto) {
        FoncierBati entity = convertToEntity(dto);
        FoncierBati saved = service.save(entity);
        return ResponseEntity.ok(new FoncierBatiDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoncierBatiDto> update(@PathVariable Long id, @RequestBody FoncierBatiDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        FoncierBati entity = convertToEntity(dto);
        entity.setId(id);
        FoncierBati updated = service.save(entity);
        return ResponseEntity.ok(new FoncierBatiDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private FoncierBati convertToEntity(FoncierBatiDto dto) {
        FoncierBati entity = new FoncierBati();
        entity.setId(dto.getId());
        entity.setNature(dto.getNature());
        entity.setAnneeConstruction(dto.getAnneeConstruction());
        entity.setModeAcquisition(dto.getModeAcquisition());
        entity.setReferencesCadastrales(dto.getReferencesCadastrales());
        entity.setSuperficie(dto.getSuperficie());
        entity.setLocalis(dto.getLocalis());
        entity.setTypeUsage(dto.getTypeUsage());
        entity.setCoutAcquisitionFCFA(dto.getCoutAcquisitionFCFA());
        entity.setCoutInvestissements(dto.getCoutInvestissements());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese());
        entity.setIdDeclaration(dto.getIdDeclaration());
        return entity;
    }
}
