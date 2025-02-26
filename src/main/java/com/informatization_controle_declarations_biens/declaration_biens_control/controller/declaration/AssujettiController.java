package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;


import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AssujettiProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAssujettiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/assujetti")
public class AssujettiController {

    @Autowired
    private IAssujettiService assujettiService;

    @GetMapping
    public List<Assujetti> getAllAssujetti() {
        return assujettiService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assujetti> getAssujettiById(@PathVariable Long id) {
        Optional<Assujetti> assujetti = assujettiService.findById(id);
        return assujetti.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Assujetti saveAssujetti(@RequestBody Assujetti assujetti) {
        return assujettiService.save(assujetti);
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

    @GetMapping("/code/{code}")
    public List<Assujetti> getAssujettiByCode(@PathVariable String code) {
        return assujettiService.findByCode(code);
    }

    @GetMapping("/details/{id}")
    public List<AssujettiProjection> getAssujettiDetails(@PathVariable Long id) {
        return assujettiService.getAssujettiDetails(id);
    }

    @GetMapping("/nom/{nom}")
    public List<Assujetti> getAssujettiByNom(@PathVariable String nom) {
        return assujettiService.findByNom(nom);
    }
    
    // New endpoints for additional search options
    @GetMapping("/email/{email}")
    public List<Assujetti> getAssujettiByEmail(@PathVariable String email) {
        return assujettiService.findByEmail(email);
    }
    
    // @GetMapping("/fonction/{fonctionLibelle}")
    // public List<Assujetti> getAssujettiByFonction(@PathVariable String fonctionLibelle) {
    //     return assujettiService.findByFonction(fonctionLibelle);
    // }
    
    // @GetMapping("/institution/{institutionLibelle}")
    // public List<Assujetti> getAssujettiByInstitution(@PathVariable String institutionLibelle) {
    //     return assujettiService.findByInstitution(institutionLibelle);
    // }
    
}


