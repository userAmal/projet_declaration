package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.MeublesMeublantsDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.MeublesMeublants;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IMeublesMeublantsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meubles-meublants")
public class MeublesMeublantsController {

    private final IMeublesMeublantsService service;

    public MeublesMeublantsController(IMeublesMeublantsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MeublesMeublantsDto>> getAll() {
        List<MeublesMeublants> list = service.findAll();
        List<MeublesMeublantsDto> dtos = list.stream()
                .map(MeublesMeublantsDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeublesMeublantsDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(MeublesMeublantsDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MeublesMeublantsDto> create(@RequestBody MeublesMeublantsDto dto) {
        MeublesMeublants entity = convertToEntity(dto);
        MeublesMeublants saved = service.save(entity);
        return ResponseEntity.ok(new MeublesMeublantsDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeublesMeublantsDto> update(@PathVariable Long id, @RequestBody MeublesMeublantsDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        MeublesMeublants entity = convertToEntity(dto);
        entity.setId(id);
        MeublesMeublants updated = service.save(entity);
        return ResponseEntity.ok(new MeublesMeublantsDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private MeublesMeublants convertToEntity(MeublesMeublantsDto dto) {
        MeublesMeublants entity = new MeublesMeublants();
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
