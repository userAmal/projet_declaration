package com.informatization_controle_declarations_biens.declaration_biens_control.controller.control;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Conclusion;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle.IConclusionService;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDeclarationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/conclusions")
public class ConclusionController {

    private final IConclusionService conclusionService;
    private final IDeclarationService declarationService;
    private final IUtilisateurService utilisateurService;

    public ConclusionController(IConclusionService conclusionService,
                              IDeclarationService declarationService,
                              IUtilisateurService utilisateurService) {
        this.conclusionService = conclusionService;
        this.declarationService = declarationService;
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/generer/{declarationId}")
    public Conclusion genererConclusion(
            @PathVariable Long declarationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam boolean estAcceptation) throws IOException {
        
        Declaration declaration = declarationService.findById(declarationId)
                .orElseThrow(() -> new RuntimeException("Déclaration non trouvée avec l'ID: " + declarationId));
        
        return conclusionService.genererConclusion(
            declaration, 
            file.getBytes(), 
            file.getOriginalFilename(),
            estAcceptation); // Ajout du paramètre
    }

    @PostMapping("/lettre/generer")
    public Conclusion genererLettreOfficielle(
            @RequestParam Long utilisateurId,
            @RequestParam Long declarationId,
            @RequestParam String contenuUtilisateur,
            @RequestParam boolean estAcceptation) {
        
        // Debug (optionnel)
        System.out.println("Reçu utilisateurId: " + utilisateurId);
        System.out.println("Reçu declarationId: " + declarationId);
        System.out.println("Reçu contenu: " + contenuUtilisateur);
        System.out.println("Type de réquisitoire: " + (estAcceptation ? "Acceptation" : "Refus"));
        
        Utilisateur utilisateur = utilisateurService.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Declaration declaration = declarationService.findById(declarationId)
                .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));
        
        return conclusionService.genererLettreOfficielle(
            utilisateur, 
            declaration, 
            contenuUtilisateur,
            estAcceptation); // Ajout du paramètre
    }

    @GetMapping("/telecharger/{conclusionId}")
    public ResponseEntity<Resource> telechargerConclusion(@PathVariable Long conclusionId) {
        return conclusionService.telechargerConclusion(conclusionId);
    }

    @GetMapping("/declaration/{declarationId}")
    public List<Conclusion> getConclusionsByDeclaration(@PathVariable Long declarationId) {
        return conclusionService.getConclusionsByDeclaration(declarationId);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public List<Conclusion> getConclusionsByUtilisateur(@PathVariable Long utilisateurId) {
        return conclusionService.getConclusionsByUtilisateur(utilisateurId);
    }

    @GetMapping("/{id}")
    public Conclusion getConclusionById(@PathVariable Long id) {
        return conclusionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Conclusion non trouvée avec l'ID: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConclusion(@PathVariable Long id) {
        try {
            conclusionService.deleteConclusion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/utilisateur/{utilisateurId}/declaration/{declarationId}")
    public List<Conclusion> getByUtilisateurAndDeclaration(
            @PathVariable Long utilisateurId,
            @PathVariable Long declarationId) {
        return conclusionService.findByUtilisateurAndDeclaration(utilisateurId, declarationId);
    }
}