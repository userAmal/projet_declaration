package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.DisponibilitesEnBanque;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDisponibilitesEnBanqueService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.DisponibilitesEnBanqueProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/disponibilites-en-banque")
public class DisponibilitesEnBanqueController {

    private final IDisponibilitesEnBanqueService service;

    public DisponibilitesEnBanqueController(IDisponibilitesEnBanqueService service) {
        this.service = service;
    }

    @GetMapping
    public List<DisponibilitesEnBanque> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilitesEnBanque> getById(@PathVariable Long id) {
        Optional<DisponibilitesEnBanque> entity = service.findById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/declaration/{declarationId}")
    public List<DisponibilitesEnBanqueProjection> getByDeclaration(@PathVariable Long declarationId) {
        return service.getByDeclaration(declarationId);
    }

    @PostMapping
    public DisponibilitesEnBanque create(@RequestBody DisponibilitesEnBanque entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilitesEnBanque> update(@PathVariable Long id, @RequestBody DisponibilitesEnBanque entity) {
        if (service.findById(id).isPresent()) {
            return ResponseEntity.ok(service.save(entity));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
