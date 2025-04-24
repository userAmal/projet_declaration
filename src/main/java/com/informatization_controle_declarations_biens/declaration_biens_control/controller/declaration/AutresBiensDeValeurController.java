package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.AutresBiensDeValeurDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresBiensDeValeur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAutresBiensDeValeurService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresBiensDeValeurProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/autres-biens-de-valeur")
public class AutresBiensDeValeurController {

    private final IAutresBiensDeValeurService service;

    public AutresBiensDeValeurController(IAutresBiensDeValeurService service) {
        this.service = service;
    }

    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<AutresBiensDeValeurDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<AutresBiensDeValeurProjection> projections = service.getByDeclaration(declarationId);
        List<AutresBiensDeValeurDto> dtos = projections.stream()
                .map(AutresBiensDeValeurDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

   
    @GetMapping("/{id}")
    public ResponseEntity<AutresBiensDeValeurDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(AutresBiensDeValeurDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AutresBiensDeValeurDto> create(@RequestBody AutresBiensDeValeurDto dto) {
        AutresBiensDeValeur entity = convertToEntity(dto);
        AutresBiensDeValeur saved = service.save(entity);
        return ResponseEntity.ok(new AutresBiensDeValeurDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutresBiensDeValeurDto> update(@PathVariable Long id, @RequestBody AutresBiensDeValeurDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        AutresBiensDeValeur entity = convertToEntity(dto);
        entity.setId(id);
        AutresBiensDeValeur updated = service.save(entity);
        return ResponseEntity.ok(new AutresBiensDeValeurDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    private AutresBiensDeValeur convertToEntity(AutresBiensDeValeurDto dto) {
        AutresBiensDeValeur entity = new AutresBiensDeValeur();
        entity.setId(dto.getId());
        entity.setDesignation(dto.getDesignation());
        entity.setLocalite(dto.getLocalite());
        entity.setAnneeAcquis(dto.getAnneeAcquis());
        entity.setValeurAcquisition(dto.getValeurAcquisition());
        entity.setAutrePrecisions(dto.getAutrePrecisions());
        entity.setType(dto.getType());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese());
            if (dto.getIdDeclaration() != null) {
            entity.setIdDeclaration(dto.getIdDeclaration());
        } else {
            throw new IllegalArgumentException("La déclaration est obligatoire");
        }
        
        return entity;
    }
    
}
