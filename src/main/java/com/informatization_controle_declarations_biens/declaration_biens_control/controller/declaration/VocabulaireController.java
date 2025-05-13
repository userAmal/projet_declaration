package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;


import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IVocabulaireService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VocabulaireProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vocabulaire")
public class VocabulaireController {

    @Autowired
    private IVocabulaireService vocabulaireService;

    @GetMapping
    public ResponseEntity<List<Vocabulaire>> getAllVocabulaire() {
        return ResponseEntity.ok(vocabulaireService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vocabulaire> getVocabulaireById(@PathVariable Long id) {
        Optional<Vocabulaire> vocabulaire = vocabulaireService.findById(id);
        return vocabulaire.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

/*     @PostMapping
    public ResponseEntity<Vocabulaire> createVocabulaire(@RequestBody Vocabulaire vocabulaire) {
        return ResponseEntity.ok(vocabulaireService.save(vocabulaire));
    }
 */

 @PostMapping
    public ResponseEntity<Object> createVocabulaire(@RequestBody Vocabulaire vocabulaire) {
        // Vérifie si le vocabulaire avec le même intitule et typeVocabulaire existe déjà
        if (vocabulaireService.existsByIntituleAndTypeVocabulaire(vocabulaire.getIntitule(), vocabulaire.getTypevocabulaire().getId())) {
            // Retourne une réponse avec un message d'erreur
            return ResponseEntity.badRequest().body("Le vocabulaire avec cet intitulé et ce type existe déjà.");
    }
    
    // Si non existant, crée et sauvegarde le vocabulaire
    Vocabulaire savedVocabulaire = vocabulaireService.save(vocabulaire);
    return ResponseEntity.ok(savedVocabulaire);
}

 /*    @PutMapping("/{id}")
    public ResponseEntity<Vocabulaire> updateVocabulaire(@PathVariable Long id, @RequestBody Vocabulaire vocabulaireDetails) {
        if (!vocabulaireService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        vocabulaireDetails.setId(id);
        return ResponseEntity.ok(vocabulaireService.save(vocabulaireDetails));
    } */

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateVocabulaire(@PathVariable Long id, @RequestBody Vocabulaire vocabulaireDetails) {
        // Vérifier si le vocabulaire à mettre à jour existe
        if (!vocabulaireService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();  // Si le vocabulaire n'existe pas, retour 404
        }
    
        // Vérifier si un autre vocabulaire avec le même intitulé et type existe déjà, sauf celui que l'on met à jour
        if (vocabulaireService.existsByIntituleAndTypeVocabulaireAndIdNot(vocabulaireDetails.getIntitule(), vocabulaireDetails.getTypevocabulaire().getId(), id)) {
            // Si un vocabulaire avec le même intitulé et type existe, retourner une erreur
            return ResponseEntity.badRequest().body("Le vocabulaire avec cet intitulé et ce type existe déjà.");
        }
    
        // Si tout est correct, on effectue la mise à jour
        vocabulaireDetails.setId(id); // On conserve l'ID pour la mise à jour
        return ResponseEntity.ok(vocabulaireService.save(vocabulaireDetails)); // Sauvegarder et retourner le vocabulaire mis à jour
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVocabulaire(@PathVariable Long id) {
        if (!vocabulaireService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        vocabulaireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Vocabulaire>> searchVocabulaire(@RequestParam String intitule) {
        return ResponseEntity.ok(vocabulaireService.findByIntitule(intitule));
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<Vocabulaire>> getByTypeVocabulaire(@PathVariable Long typeId) {
        return ResponseEntity.ok(vocabulaireService.findByTypeVocabulaire(typeId));
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Vocabulaire>> getByVocabulaireParent(@PathVariable Long parentId) {
        return ResponseEntity.ok(vocabulaireService.findByVocabulaireParent(parentId));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<List<VocabulaireProjection>> getVocabulaireDetails(@PathVariable Long id) {
        return ResponseEntity.ok(vocabulaireService.getVocabulaireDetails(id));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<Void> disableVocabulaire(@PathVariable Long id) {
        Optional<Vocabulaire> vocabulaire = vocabulaireService.findById(id);
        if (vocabulaire.isPresent()) {
            Vocabulaire v = vocabulaire.get();
            v.setActive(false);
            vocabulaireService.save(v);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PutMapping("/{id}/enable")
    public ResponseEntity<Void> enableVocabulaire(@PathVariable Long id) {
        Optional<Vocabulaire> vocabulaire = vocabulaireService.findById(id);
        if (vocabulaire.isPresent()) {
            Vocabulaire v = vocabulaire.get();
            v.setActive(true); 
            vocabulaireService.save(v);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
