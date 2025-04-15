package com.informatization_controle_declarations_biens.declaration_biens_control.controller.parametrage;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.parametrage.ParametrageDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.parametrage.Parametrage;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.parametrage.IParametrageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parametrages")
public class ParametrageController {

    @Autowired
    private IParametrageService parametrageService;

@GetMapping
    public ResponseEntity<List<Parametrage>> getAllParametrage() {
        return ResponseEntity.ok(parametrageService.findAll());
    }
    
    @GetMapping("/{id}")
        public ResponseEntity<ParametrageDto> getParametrage(@PathVariable Long id) {
            return parametrageService.findById(id)
                    .map(parametrage -> new ParametrageDto(parametrage)) 
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @PutMapping("/{id}/valeur")
        public ResponseEntity<ParametrageDto> updateValeur(@PathVariable Long id, @RequestBody Parametrage parametrage) {
            System.out.println("Valeur reçue : " + parametrage.getValeur());
        
          
            Parametrage updatedParametrage = parametrageService.updateValeur(id, parametrage.getValeur());
            
            if (updatedParametrage != null) {
                return ResponseEntity.ok(new ParametrageDto(updatedParametrage));  
            }
            return ResponseEntity.notFound().build(); 
        }
        
        @GetMapping("/search")
        public ResponseEntity<ParametrageDto> getParametrageByCode(@RequestParam String code) {
            Parametrage parametrage = parametrageService.getByCode(code);
            if (parametrage != null) {
                return ResponseEntity.ok(new ParametrageDto(parametrage));
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        

        

        



}
