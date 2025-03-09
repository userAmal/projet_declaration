package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.FoncierNonBatiDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierNonBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IFoncierNonBatiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierNonBatiProjection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/foncier-non-bati")
public class FoncierNonBatiController {

    private final IFoncierNonBatiService foncierNonBatiService;

    public FoncierNonBatiController(IFoncierNonBatiService foncierNonBatiService) {
        this.foncierNonBatiService = foncierNonBatiService;
    }

    @GetMapping
    public ResponseEntity<List<FoncierNonBatiDto>> getAllFoncierNonBati() {
        List<FoncierNonBati> list = foncierNonBatiService.findAll();
        List<FoncierNonBatiDto> dtos = list.stream()
                .map(FoncierNonBatiDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<FoncierNonBatiDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<FoncierNonBatiProjection> projections = foncierNonBatiService.getByDeclaration(declarationId);
        List<FoncierNonBatiDto> dtos = projections.stream()
                .map(FoncierNonBatiDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FoncierNonBatiDto> getFoncierNonBatiById(@PathVariable Long id) {
        return foncierNonBatiService.findById(id)
                .map(FoncierNonBatiDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FoncierNonBatiDto> createFoncierNonBati(@RequestBody FoncierNonBatiDto dto) {
        FoncierNonBati entity = convertToEntity(dto);
        FoncierNonBati saved = foncierNonBatiService.save(entity);
        return ResponseEntity.ok(new FoncierNonBatiDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoncierNonBatiDto> updateFoncierNonBati(@PathVariable Long id, @RequestBody FoncierNonBatiDto dto) {
        if (!foncierNonBatiService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        FoncierNonBati entity = convertToEntity(dto);
        entity.setId(id);
        FoncierNonBati updated = foncierNonBatiService.save(entity);
        return ResponseEntity.ok(new FoncierNonBatiDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoncierNonBati(@PathVariable Long id) {
        if (!foncierNonBatiService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        foncierNonBatiService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private FoncierNonBati convertToEntity(FoncierNonBatiDto dto) {
        FoncierNonBati entity = new FoncierNonBati();
        entity.setId(dto.getId());
        entity.setNature(dto.getNature()); 
        entity.setModeAcquisition(dto.getModeAcquisition()); 
        entity.setIlot(dto.getIlot());
        entity.setLotissement(dto.getLotissement());
        entity.setSuperficie(dto.getSuperficie());
        entity.setLocalite(dto.getLocalite());
        entity.setTitrePropriete(dto.getTitrePropriete());
        entity.setDateAcquis(dto.getDateAcquis());
        entity.setValeurAcquisFCFA(dto.getValeurAcquisFCFA());
        entity.setCoutInvestissements(dto.getCoutInvestissements());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese()); 
        entity.setIdDeclaration(dto.getIdDeclaration()); 
    
        return entity;
    }
    
}
