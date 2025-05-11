package com.informatization_controle_declarations_biens.declaration_biens_control.controller.control;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle.IRapportService;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDeclarationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.DeclarationService;

import org.springframework.core.io.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rapports")
public class RapportController {

    @Autowired
    private IRapportService rapportService;
     @Autowired
    private IUtilisateurService utilisateurService;
    
    @Autowired
    private DeclarationService declarationService;

     @PostMapping("/provisoire")
    public ResponseEntity<Rapport> genererProvisoire(
            @RequestParam Long utilisateurId,
            @RequestParam Long declarationId,
            @RequestBody String contenu) {
        
        // Récupération de l'utilisateur
        Utilisateur utilisateur = utilisateurService.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + utilisateurId));
        
        // Récupération de la déclaration
        Declaration declaration = declarationService.findById(declarationId)
                .orElseThrow(() -> new RuntimeException("Déclaration non trouvée avec l'ID: " + declarationId));
        
        // Génération du rapport provisoire
        Rapport rapport = rapportService.genererRapportProvisoire(utilisateur, declaration, contenu);
        
        return ResponseEntity.ok(rapport);
    }

    @PostMapping("/definitif")
    public ResponseEntity<Rapport> genererDefinitif(
            @RequestParam Long utilisateurId,
            @RequestParam Long declarationId,
            @RequestParam Boolean decision,
            @RequestBody String contenu) {
        
        Utilisateur utilisateur = utilisateurService.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Declaration declaration = declarationService.findById(declarationId)
                .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));
        
        Rapport rapport = rapportService.genererRapportDefinitif(utilisateur, declaration, decision, contenu);
        return ResponseEntity.ok(rapport);
    }
    
    @GetMapping("/telecharger/{id}")
    public ResponseEntity<Resource> telecharger(@PathVariable Long id) {
        return rapportService.telechargerRapport(id);
    }

    @GetMapping("/declaration/{declarationId}")
    public List<Rapport> getByDeclaration(@PathVariable Long declarationId) {
        return rapportService.getRapportsByDeclaration(declarationId);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public List<Rapport> getByUtilisateur(@PathVariable Long utilisateurId) {
        return rapportService.getRapportsByUtilisateur(utilisateurId);
    }

    @GetMapping("/type/{type}")
    public List<Rapport> getByType(@PathVariable Rapport.Type type) {
        return rapportService.getRapportsByType(type);
    }

    @GetMapping("/{id}")
    public Rapport getById(@PathVariable Long id) {
        return rapportService.getRapportById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        rapportService.deleteRapport(id);
    }
}