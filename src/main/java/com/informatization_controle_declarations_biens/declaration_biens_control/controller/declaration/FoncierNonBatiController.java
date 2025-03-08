package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.FoncierNonBatiDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierNonBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IFoncierNonBatiService;
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
        entity.setNature(dto.getNature()); // Remplace type par nature
        entity.setSuperficie(dto.getSuperficie());
        return entity;
    }
}
