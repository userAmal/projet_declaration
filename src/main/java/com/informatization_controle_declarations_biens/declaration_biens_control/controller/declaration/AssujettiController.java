package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAssujettiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AssujettiProjection;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assujetti")
public class AssujettiController {

    private static final Logger logger = LoggerFactory.getLogger(AssujettiController.class);

    @Autowired
    private IAssujettiService assujettiService;

    @GetMapping
    public List<Assujetti> getAllAssujetti() {
        return assujettiService.findAll();
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<?> restoreAssujetti(@PathVariable Long id) {
        try {
            assujettiService.restoreAssujetti(id);
            return ResponseEntity.ok().body(Map.of("message", "Assujetti restauré avec succès"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la restauration de l'assujetti: " + e.getMessage()));
        }
    }


    @GetMapping("/stopped")
    public List<Assujetti> getAllStoppedAssujetti() {
        return assujettiService.findAllStopped();
    }
    @GetMapping("/declaration/access")
    public ResponseEntity<Long> getDeclarationIdFromToken(@RequestParam String token) {
        // Utilisation du logger pour afficher le token
        logger.info("Token reçu: " + token);
        Long declarationId = assujettiService.verifyToken(token);
        if (declarationId != null) {
            return ResponseEntity.ok(declarationId);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    



    @GetMapping("/{id}")
    public ResponseEntity<Assujetti> getAssujettiById(@PathVariable Long id) {
        Optional<Assujetti> assujetti = assujettiService.findById(id);
        return assujetti.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }
    


/*     @PostMapping
    public Assujetti saveAssujetti(@RequestBody Assujetti assujetti) {
        return assujettiService.save(assujetti);
    } */

/*      @PostMapping
    public ResponseEntity<?> saveAssujetti(@RequestBody @Valid Assujetti assujetti, BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        Assujetti saved = assujettiService.save(assujetti);
        return ResponseEntity.ok(saved);
    }  */


    @PostMapping
    public ResponseEntity<Assujetti> saveAssujetti(@RequestBody @Valid Assujetti assujetti, BindingResult result) {
        if (result.hasErrors()) {
            // Collecter toutes les erreurs et les envoyer dans la réponse
            String errorMessage = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            // Retourner une réponse 400 Bad Request avec un message détaillé
            return ResponseEntity.badRequest().body(null);  // Tu pourrais aussi inclure errorMessage dans la réponse
        }
    
        try {
            System.out.println("Erreurs: " + result.getAllErrors());
            Assujetti savedAssuj = assujettiService.save(assujetti);
            return ResponseEntity.ok(savedAssuj);
        } catch (Exception e) {
            // Si une exception se produit lors de la sauvegarde, retourner 500
            return ResponseEntity.status(500).body(null);  
        }
    }
    
    


    @PutMapping("/{id}")
    public ResponseEntity<Assujetti> updateAssujetti(@PathVariable Long id, @RequestBody Assujetti assujetti) {
        Optional<Assujetti> existingAssujettiOpt = assujettiService.findById(id);
        if (existingAssujettiOpt.isPresent()) {
            Assujetti existingAssujetti = existingAssujettiOpt.get();
            existingAssujetti.setCivilite(assujetti.getCivilite());
            existingAssujetti.setNom(assujetti.getNom());
            existingAssujetti.setPrenom(assujetti.getPrenom());
            existingAssujetti.setContacttel(assujetti.getContacttel());
            existingAssujetti.setEmail(assujetti.getEmail());
            existingAssujetti.setCode(assujetti.getCode());
            existingAssujetti.setEtat(assujetti.getEtat());
            existingAssujetti.setInstitutions(assujetti.getInstitutions());
            existingAssujetti.setAdministration(assujetti.getAdministration());
            existingAssujetti.setEntite(assujetti.getEntite());
            existingAssujetti.setFonction(assujetti.getFonction());
            existingAssujetti.setMatricule(assujetti.getMatricule());
            existingAssujetti.setDatePriseDeService(assujetti.getDatePriseDeService());
            existingAssujetti.setDateCessationFonction(assujetti.getDateCessationFonction());
            
            return ResponseEntity.ok(assujettiService.save(existingAssujetti));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssujetti(@PathVariable Long id) {
        Assujetti existingAssujetti = assujettiService.findById(id).orElse(null);
        if (existingAssujetti != null) {
            assujettiService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/details/{id}")
    public List<AssujettiProjection> getAssujettiDetails(@PathVariable Long id) {
        return assujettiService.getAssujettiDetails(id);
    }

    @GetMapping("/nom/{nom}")
    public List<Assujetti> getAssujettiByNom(@PathVariable String nom) {
        return assujettiService.findByNom(nom);
    }
    
    @GetMapping("/email/{email}")
    public List<Assujetti> getAssujettiByEmail(@PathVariable String email) {
        return assujettiService.findByEmail(email);
    }

    /* @PutMapping("/{id}/archiver")
    public ResponseEntity<Void> archiverAssujetti(@PathVariable Long id) {
        try {
            assujettiService.archiverAssujetti(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    } */

        @PutMapping("/{id}/archiver")
        public ResponseEntity<?> archiverUtilisateur(@PathVariable Long id) {
            try {
                assujettiService.archiverAssujetti(id);
                return ResponseEntity.noContent().build();
            } catch (IllegalStateException e) {
                return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Erreur interne du serveur");
            }
        }

    

}
