package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.MagicTokenRepository;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.MagicToken;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDeclarationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/declarations")
public class DeclarationController {
    
    private final IDeclarationService declarationService;

    public DeclarationController(IDeclarationService declarationService) {
        this.declarationService = declarationService;
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
    @Autowired
    private MagicTokenRepository magicTokenRepository;
    
    @GetMapping("/access")
    public ResponseEntity<?> validateMagicToken(@RequestParam String token) {
        MagicToken magicToken = magicTokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Token invalide"));
        
        if (magicToken.isUsed()) {
            throw new RuntimeException("Token déjà utilisé");
        }
        
        if (magicToken.getExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expiré");
        }
        
        // Marquer le token comme utilisé
        magicToken.setUsed(true);
        magicTokenRepository.save(magicToken);
        
        return ResponseEntity.ok()
            .header("Location", "/declaration-form")
            .build();
    }
}