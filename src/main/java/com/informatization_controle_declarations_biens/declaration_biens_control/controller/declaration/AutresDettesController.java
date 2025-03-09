package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresDettes;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAutresDettesService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresDettesProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/autres-dettes")
public class AutresDettesController {

    private final IAutresDettesService service;

    public AutresDettesController(IAutresDettesService service) {
        this.service = service;
    }

    @GetMapping
    public List<AutresDettes> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutresDettes> getById(@PathVariable Long id) {
        Optional<AutresDettes> entity = service.findById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-declaration/{declarationId}")
    public List<AutresDettesProjection> getByDeclaration(@PathVariable Long declarationId) {
        return service.getByDeclaration(declarationId);
    }

    @PostMapping
    public AutresDettes create(@RequestBody AutresDettes entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutresDettes> update(@PathVariable Long id, @RequestBody AutresDettes entity) {
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
