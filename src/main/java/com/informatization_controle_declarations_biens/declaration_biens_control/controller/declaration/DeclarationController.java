package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDeclarationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.AssujettiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/declarations")
public class DeclarationController {
    @Autowired
    private final IDeclarationService declarationService;
    @Autowired
    private final AssujettiService assujettiService;
   
    public DeclarationController(IDeclarationService declarationService, AssujettiService assujettiService) {
        this.declarationService = declarationService;
        this.assujettiService = assujettiService;
    }

    // Endpoint corrigé pour récupérer l'assujetti à partir de l'ID de déclaration
    @GetMapping("/{id}/assujetti")
    public ResponseEntity<Assujetti> getAssujettiByDeclarationId(@PathVariable Long id) {
        // Récupérer la déclaration par son ID
        Optional<Declaration> declaration = declarationService.findById(id);
        
        if (!declaration.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Récupérer directement l'objet assujetti à partir de la déclaration
        Assujetti assujetti = declaration.get().getAssujetti();
        
        if (assujetti == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
        }
        
        return ResponseEntity.ok(assujetti);
    }
    
    
    @GetMapping("/{id}/details")
    public ResponseEntity<DeclarationDto> getFullDeclarationDetails(@PathVariable Long id) {
        DeclarationDto declarationDetails = declarationService.getFullDeclarationDetails(id);
        if (declarationDetails != null) {
            return ResponseEntity.ok(declarationDetails);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping
    public List<Declaration> getAllDeclarations() {
        return declarationService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Declaration> getDeclarationById(@PathVariable Long id) {
        Optional<Declaration> declaration = declarationService.findById(id);
        return declaration.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Declaration createDeclaration(@RequestBody Declaration declaration) {
        return declarationService.save(declaration);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Declaration> updateDeclaration(@PathVariable Long id, @RequestBody Declaration declaration) {
        Optional<Declaration> existingDeclaration = declarationService.findById(id);
        if (existingDeclaration.isPresent()) {
            declaration.setId(id);
            return ResponseEntity.ok(declarationService.save(declaration));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeclaration(@PathVariable Long id) {
        if (declarationService.findById(id).isPresent()) {
            declarationService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/access")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        Long declarationId = assujettiService.verifyToken(token);
       
        if (declarationId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Lien invalide ou expiré");
        }
       
        Optional<Declaration> declaration = declarationService.findById(declarationId);
        if (!declaration.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Déclaration non trouvée");
        }
       
        return ResponseEntity.ok()
            .header("Location", "/declaration-form?id=" + declarationId)
            .body(Map.of("declarationId", declarationId));
    }
}