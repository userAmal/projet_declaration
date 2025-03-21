package com.informatization_controle_declarations_biens.declaration_biens_control.controller.parametrage;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.parametrage.ParametrageDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.parametrage.Parametrage;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.parametrage.IParametrageService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.parametrage.ParametrageProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                    .map(parametrage -> new ParametrageDto(parametrage)) // Conversion de Parametrage en ParametrageDto
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @PutMapping("/{id}/valeur")
        public ResponseEntity<ParametrageDto> updateValeur(@PathVariable Long id, @RequestBody Parametrage parametrage) {
            // Vérifier la valeur reçue directement dans l'entité
            System.out.println("Valeur reçue : " + parametrage.getValeur());
        
            // Mise à jour de l'entité Parametrage avec la nouvelle valeur
            Parametrage updatedParametrage = parametrageService.updateValeur(id, parametrage.getValeur());
            
            if (updatedParametrage != null) {
                return ResponseEntity.ok(new ParametrageDto(updatedParametrage));  // Retourner le Parametrage mis à jour
            }
            return ResponseEntity.notFound().build();  // Retourner une réponse 404 si l'entité n'est pas trouvée
        }
        

        



}
