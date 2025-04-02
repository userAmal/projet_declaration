package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.parametrage.Parametrage;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDeclarationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.control.PdfFileService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.AssujettiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.parametrage.ParametrageService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/declarations")
public class DeclarationController {
    @Autowired
    private ParametrageService parametrageService;
    
    private final PdfFileService pdfFileService;
    @Autowired
    private final IDeclarationService declarationService;
    @Autowired
    private final AssujettiService assujettiService;
   
    public DeclarationController(IDeclarationService declarationService, AssujettiService assujettiService, PdfFileService pdfFileService) {
        this.declarationService = declarationService;
        this.assujettiService = assujettiService;
        this.pdfFileService = pdfFileService;
    }
    
    @GetMapping("/{id}/generate-pdf")
    public ResponseEntity<Resource> generateFullDeclarationPdf(@PathVariable Long id) throws IOException {
        DeclarationDto declarationDto = declarationService.getFullDeclarationDetails(id);
    
        if (declarationDto == null || declarationDto.getAssujetti() == null) {
            return ResponseEntity.notFound().build();
        }
    
        // Utilisation de l'instance injectée de ParametrageService
        Parametrage documentsPath = parametrageService.getByCode("PATH_DOCUMENTS_ASSUJETTIS");
    
        if (documentsPath == null) {
            throw new RuntimeException("Paramètre PATH_DOCUMENTS_ASSUJETTIS non configuré");
        }
    
        // Le reste de la méthode reste inchangé...
        Path outputPath = Paths.get(documentsPath.getValeur());
        Files.createDirectories(outputPath);
    
        String fileName = generateFileName(declarationDto);
        Path fullPath = outputPath.resolve(fileName + ".pdf");
    
        pdfFileService.generateFullDeclarationPdf(declarationDto, fullPath.toString());
    
        Resource resource = new FileSystemResource(fullPath.toFile());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
    
    private String generateFileName(DeclarationDto declarationDto) {
        return "declaration_" + declarationDto.getId() + "_" +
               new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }
    
    @GetMapping("/{id}/assujetti")
    public ResponseEntity<Assujetti> getAssujettiByDeclarationId(@PathVariable Long id) {
        Optional<Declaration> declaration = declarationService.findById(id);
        
        if (!declaration.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
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
    @PostMapping("/{id}/validate")
public ResponseEntity<?> validateDeclaration(@PathVariable Long id) {
    try {
        Declaration declaration = declarationService.validateDeclaration(id);
        return ResponseEntity.ok(Map.of(
            "message", "Déclaration validée avec succès",
            "declaration", declaration
        ));
    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Erreur interne du serveur"));
    }
}

@PostMapping("/{id}/refuse")
public ResponseEntity<?> refuseDeclaration(@PathVariable Long id) {
    try {
        Declaration declaration = declarationService.refuseDeclaration(id);
        return ResponseEntity.ok(Map.of(
            "message", "Déclaration refusée avec succès",
            "declaration", declaration
        ));
    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Erreur interne du serveur"));
    }
}
}