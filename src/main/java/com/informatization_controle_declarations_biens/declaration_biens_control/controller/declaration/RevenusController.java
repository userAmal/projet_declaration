package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.RevenusDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Revenus;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IRevenusService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.RevenusProjection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/revenus")
public class RevenusController {

    private final IRevenusService revenusService;

    public RevenusController(IRevenusService revenusService) {
        this.revenusService = revenusService;
    }

    @GetMapping
    public ResponseEntity<List<RevenusDto>> getAllRevenus() {
        List<Revenus> list = revenusService.findAll();
        List<RevenusDto> dtos = list.stream()
                .map(RevenusDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<RevenusDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<RevenusProjection> projections = revenusService.getByDeclaration(declarationId);
        List<RevenusDto> dtos = projections.stream()
                .map(RevenusDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<RevenusDto> getRevenusById(@PathVariable Long id) {
        return revenusService.findById(id)
                .map(RevenusDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RevenusDto> createRevenus(@RequestBody RevenusDto dto) {
        Revenus entity = convertToEntity(dto);
        Revenus saved = revenusService.save(entity);
        return ResponseEntity.ok(new RevenusDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RevenusDto> updateRevenus(@PathVariable Long id, @RequestBody RevenusDto dto) {
        if (!revenusService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Revenus entity = convertToEntity(dto);
        entity.setId(id);
        Revenus updated = revenusService.save(entity);
        return ResponseEntity.ok(new RevenusDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRevenus(@PathVariable Long id) {
        if (!revenusService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        revenusService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Revenus convertToEntity(RevenusDto dto) {
        Revenus entity = new Revenus();
        entity.setId(dto.getId());
        entity.setAutresRevenus(dto.getAutresRevenus()); 
        entity.setDateCreation(dto.getDateCreation());
        entity.setIdDeclaration(dto.getIdDeclaration());
        return entity;
    }
    
}
