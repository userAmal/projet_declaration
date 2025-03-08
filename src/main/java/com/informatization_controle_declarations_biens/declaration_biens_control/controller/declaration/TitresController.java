package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.TitresDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Titres;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.ITitresService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TitresProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/titres")
public class TitresController {

    private final ITitresService titresService;

    public TitresController(ITitresService titresService) {
        this.titresService = titresService;
    }

    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<TitresDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<TitresProjection> projections = titresService.getByDeclaration(declarationId);
        List<TitresDto> dtos = projections.stream()
                .map(TitresDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<TitresDto>> getAll() {
        List<Titres> list = titresService.findAll();
        List<TitresDto> dtos = list.stream()
                .map(TitresDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TitresDto> getById(@PathVariable Long id) {
        return titresService.findById(id)
                .map(TitresDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TitresDto> create(@RequestBody TitresDto dto) {
        Titres entity = convertToEntity(dto);
        Titres saved = titresService.save(entity);
        return ResponseEntity.ok(new TitresDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TitresDto> update(@PathVariable Long id, @RequestBody TitresDto dto) {
        if (!titresService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Titres entity = convertToEntity(dto);
        entity.setId(id);
        Titres updated = titresService.save(entity);
        return ResponseEntity.ok(new TitresDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!titresService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        titresService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Titres convertToEntity(TitresDto dto) {
        Titres entity = new Titres();
        entity.setId(dto.getId());
        entity.setDesignationNatureActions(dto.getDesignationNatureActions());
        entity.setValeurEmplacement(dto.getValeurEmplacement());
        entity.setEmplacement(dto.getEmplacement());
        entity.setAutrePrecisions(dto.getAutrePrecisions());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese());
        entity.setIdDeclaration(dto.getIdDeclaration());
        return entity;
    }
    
}
