package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Emprunts;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IEmpruntsService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EmpruntsProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/emprunts")
public class EmpruntsController {

    private final IEmpruntsService service;

    public EmpruntsController(IEmpruntsService service) {
        this.service = service;
    }

    @GetMapping
    public List<Emprunts> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emprunts> getById(@PathVariable Long id) {
        Optional<Emprunts> entity = service.findById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/declaration/{declarationId}")
    public List<EmpruntsProjection> getByDeclaration(@PathVariable Long declarationId) {
        return service.getByDeclaration(declarationId);
    }

    @PostMapping
    public Emprunts create(@RequestBody Emprunts entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Emprunts> update(@PathVariable Long id, @RequestBody Emprunts entity) {
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
