package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.parametrage.Parametrage;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDeclarationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.control.PdfFileService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.AssujettiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.parametrage.ParametrageService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.JWTService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.UtilisateurServiceImpl;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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



import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


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
    @Autowired
    private final UtilisateurServiceImpl utilisateurService;
     @Autowired
    private JWTService jwtService;
    
   
    public DeclarationController(IDeclarationService declarationService, AssujettiService assujettiService, PdfFileService pdfFileService, UtilisateurServiceImpl utilisateurService) {
        this.declarationService = declarationService;
        this.assujettiService = assujettiService;
        this.pdfFileService = pdfFileService;
        this.utilisateurService= utilisateurService;
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
public ResponseEntity<List<Declaration>> getAllDeclarations(@AuthenticationPrincipal UserDetails userDetails) {
    // Récupérer le nom d'utilisateur connecté (email ou username en général)
    String username = userDetails.getUsername();

    // Récupérer l'utilisateur depuis la base de données
    Utilisateur utilisateur = utilisateurService.findByEmail(username)
    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    if (utilisateur == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Récupérer uniquement les déclarations liées à cet utilisateur
    List<Declaration> declarations = declarationService.getDeclarationsByUtilisateurId(utilisateur.getId());

    return ResponseEntity.ok(declarations);
}

/* 
    @GetMapping
public ResponseEntity<List<Declaration>> getDeclarationsByConnectedUser(@RequestHeader("Authorization") String authHeader) {
    try {
        // 1. Extraire le token (sans "Bearer ")
        String token = authHeader.replace("Bearer ", "");

        // 2. Extraire le username (email)
        String username = jwtService.extractUsername(token);

        // 3. Récupérer l'utilisateur connecté
        Utilisateur utilisateurConnecte = utilisateurService.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 4. Chercher les déclarations de cet utilisateur
        List<Declaration> declarations = declarationService.findByUtilisateur(utilisateurConnecte);

        return ResponseEntity.ok(declarations);

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}*/

private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername(); // Email
        }
        return null;
    }
@GetMapping("/mes-declarations")
public List<Declaration> getDeclarationsUtilisateurConnecte() {
    String email = getCurrentUserEmail();
    Utilisateur utilisateur = utilisateurService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    return declarationService.findByUtilisateur(utilisateur);
}


/* 
    @GetMapping
    public ResponseEntity<List<Declaration>> getAllDeclarations(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.extractUsername(token); // ici username = email

            Utilisateur utilisateurConnecte = utilisateurService.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            List<Declaration> declarations = declarationService.findByUtilisateur(utilisateurConnecte);
            return ResponseEntity.ok(declarations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



    @GetMapping
    public List<Declaration> getAllDeclarations() {
        return declarationService.findAll();
    }*/
    /* 
    @GetMapping("/{id}")
    public ResponseEntity<Declaration> getDeclarationById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);

        Utilisateur utilisateurConnecte = utilisateurService.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Declaration declaration = declarationService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Déclaration non trouvée"));

        if (!declaration.getUtilisateur().getId().equals(utilisateurConnecte.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(declaration);
    }*/

    
    
    
    
    @GetMapping("/{id}")
    public ResponseEntity<Declaration> getDeclarationById(@PathVariable Long id) {
        Optional<Declaration> declaration = declarationService.findById(id);
        return declaration.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }
    /* 
    @PostMapping
    public Declaration createDeclaration(@RequestBody Declaration declaration) {
        return declarationService.save(declaration);
    }*/

    @PostMapping
    public Declaration createDeclaration(@RequestBody Declaration declaration) {
        // Récupérer la liste des utilisateurs avec le rôle "Procureur Général"
        List<Utilisateur> utilisateurs = utilisateurService.findByRole(RoleEnum.procureur_general);
     
        // Vérifier si la liste est vide
        Utilisateur procureurGeneral = utilisateurs.stream()
            .findFirst() // Prendre le premier utilisateur s'il existe
            .orElseThrow(() -> new EntityNotFoundException("Procureur général non trouvé"));
    
        // Assigner cet utilisateur à la déclaration
        declaration.setUtilisateur(procureurGeneral);
    
        // Sauvegarder la déclaration
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

@PutMapping("/{declarationId}/assign-user/{utilisateurId}")
public ResponseEntity<Declaration> assignUserToDeclaration(@PathVariable Long declarationId, @PathVariable Long utilisateurId) {
    try {
        Declaration updatedDeclaration = declarationService.assignUserToDeclaration(declarationId, utilisateurId);
        return ResponseEntity.ok(updatedDeclaration);
    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}


@GetMapping("/search")
public ResponseEntity<List<Declaration>> searchDeclarationsByAssujetti(@RequestParam String keyword) {
    List<Declaration> declarations = declarationService.searchByNomOrPrenomAssujetti(keyword);
    return ResponseEntity.ok(declarations);
}
@GetMapping("/by-user/{utilisateurId}")
public ResponseEntity<List<Declaration>> getDeclarationsByUserId(@PathVariable Long utilisateurId) {
    try {
        // Récupérer l'utilisateur par son ID
        Utilisateur utilisateur = utilisateurService.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + utilisateurId));
        
        // Récupérer les déclarations de cet utilisateur
        List<Declaration> declarations = declarationService.findByUtilisateur(utilisateur);
        
        return ResponseEntity.ok(declarations);
    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}




}