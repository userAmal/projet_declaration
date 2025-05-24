package com.informatization_controle_declarations_biens.declaration_biens_control.controller.control;


import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.controle.AmendeDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Amende;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.StatutAmendeEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle.IAmendeService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.controle.AmendeProjection;

@RestController
@RequestMapping("/api/amendes")
public class AmendeController {
    
    @Autowired
    private IAmendeService amendeService;
    
    @GetMapping
    public ResponseEntity<List<AmendeDTO>> getAllAmendes() {
        List<Amende> amendes = amendeService.findAll();
        return ResponseEntity.ok(amendeService.toDTOList(amendes));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AmendeDTO> getAmendeById(@PathVariable Long id) {
        return amendeService.findById(id)
                .map(amendeService::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/projections")
    public ResponseEntity<List<AmendeProjection>> getAllAmendeProjections() {
        return ResponseEntity.ok(amendeService.findAllAmendeProjections());
    }
    
    @GetMapping("/declaration/{declarationId}")
    public ResponseEntity<List<AmendeDTO>> getAmendesByDeclarationId(@PathVariable Long declarationId) {
        List<Amende> amendes = amendeService.findByDeclarationId(declarationId);
        return ResponseEntity.ok(amendeService.toDTOList(amendes));
    }
    
    @GetMapping("/assujetti/{assujettiId}")
    public ResponseEntity<List<AmendeDTO>> getAmendesByAssujettiId(@PathVariable Long assujettiId) {
        List<Amende> amendes = amendeService.findByAssujettiId(assujettiId);
        return ResponseEntity.ok(amendeService.toDTOList(amendes));
    }
    
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<AmendeDTO>> getAmendesByStatut(@PathVariable StatutAmendeEnum statut) {
        List<Amende> amendes = amendeService.findByStatut(statut);
        return ResponseEntity.ok(amendeService.toDTOList(amendes));
    }
    
    @GetMapping("/periode")
    public ResponseEntity<List<AmendeDTO>> getAmendesByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<Amende> amendes = amendeService.findByDateAmendeBetween(debut, fin);
        return ResponseEntity.ok(amendeService.toDTOList(amendes));
    }
    
    @PostMapping
    public ResponseEntity<AmendeDTO> createAmende(@RequestBody Amende amende) {
        Amende savedAmende = amendeService.save(amende);
        return ResponseEntity.status(HttpStatus.CREATED).body(amendeService.toDTO(savedAmende));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AmendeDTO> updateAmende(@PathVariable Long id, @RequestBody Amende amende) {
        if (!amendeService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        amende.setId(id);
        Amende updatedAmende = amendeService.save(amende);
        return ResponseEntity.ok(amendeService.toDTO(updatedAmende));
    }
    
    @PutMapping("/{id}/payer")
    public ResponseEntity<Void> payerAmende(
            @PathVariable Long id,
            @RequestParam String referencePaiement) {
        try {
            amendeService.payerAmende(id, referencePaiement);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/annuler")
    public ResponseEntity<Void> annulerAmende(
            @PathVariable Long id,
            @RequestParam String motifAnnulation) {
        try {
            amendeService.annulerAmende(id, motifAnnulation);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAmende(@PathVariable Long id) {
        if (!amendeService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        amendeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}