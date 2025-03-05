package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeVocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.ITypeVocabulaireService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/type-vocabulaire")
public class TypeVocabulaireController {

    @Autowired
    private ITypeVocabulaireService typeVocabulaireService;

    @GetMapping
    public List<TypeVocabulaire> getAllTypeVocabulaire() {
        return typeVocabulaireService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeVocabulaire> getTypeVocabulaireById(@PathVariable Long id) {
        Optional<TypeVocabulaire> type = typeVocabulaireService.findById(id);
        return type.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


  





}