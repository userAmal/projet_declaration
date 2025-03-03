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

    @PostMapping
    public ResponseEntity<Vocabulaire> createVocabulaire(@RequestBody Vocabulaire vocabulaire) {
        return ResponseEntity.ok(vocabulaireService.save(vocabulaire));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vocabulaire> updateVocabulaire(@PathVariable Long id, @RequestBody Vocabulaire vocabulaireDetails) {
        if (!vocabulaireService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        vocabulaireDetails.setId(id);
        return ResponseEntity.ok(vocabulaireService.save(vocabulaireDetails));
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
}
