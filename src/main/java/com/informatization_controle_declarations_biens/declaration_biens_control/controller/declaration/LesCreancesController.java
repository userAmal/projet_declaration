package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.LesCreancesDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.LesCreances;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.ILesCreancesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/les-creances")
public class LesCreancesController {

    private final ILesCreancesService service;

    public LesCreancesController(ILesCreancesService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<LesCreancesDto>> getAll() {
        List<LesCreances> list = service.findAll();
        List<LesCreancesDto> dtos = list.stream()
                .map(LesCreancesDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LesCreancesDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(LesCreancesDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LesCreancesDto> create(@RequestBody LesCreancesDto dto) {
        LesCreances entity = convertToEntity(dto);
        LesCreances saved = service.save(entity);
        return ResponseEntity.ok(new LesCreancesDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LesCreancesDto> update(@PathVariable Long id, @RequestBody LesCreancesDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        LesCreances entity = convertToEntity(dto);
        entity.setId(id);
        LesCreances updated = service.save(entity);
        return ResponseEntity.ok(new LesCreancesDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private LesCreances convertToEntity(LesCreancesDto dto) {
        LesCreances entity = new LesCreances();
        entity.setId(dto.getId());
        entity.setMontant(dto.getMontant());
        entity.setDateCreation(dto.getDateCreation());
        return entity;
    }
}
