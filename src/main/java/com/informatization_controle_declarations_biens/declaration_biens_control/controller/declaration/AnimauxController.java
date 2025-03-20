package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.AnimauxDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Animaux;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAnimauxService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AnimauxProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/animaux")
public class AnimauxController {

    private final IAnimauxService animauxService;

    public AnimauxController(IAnimauxService animauxService) {
        this.animauxService = animauxService;
    }

    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<AnimauxDto>> getAnimauxByDeclaration(@PathVariable Long declarationId) {
        List<AnimauxProjection> projections = animauxService.getAnimauxByDeclaration(declarationId);
        List<AnimauxDto> dtos = projections.stream()
                .map(AnimauxDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<AnimauxDto>> getAllAnimaux() {
        List<Animaux> animauxList = animauxService.findAll();
        List<AnimauxDto> dtos = animauxList.stream()
                .map(AnimauxDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimauxDto> getAnimauxById(@PathVariable Long id) {
        return animauxService.findById(id)
                .map(AnimauxDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AnimauxDto> createAnimaux(@RequestBody AnimauxDto animauxDto) {
        Animaux animaux = convertToEntity(animauxDto);
        Animaux saved = animauxService.save(animaux);
        return ResponseEntity.ok(new AnimauxDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimauxDto> updateAnimaux(@PathVariable Long id, @RequestBody AnimauxDto animauxDto) {
        if (!animauxService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Animaux animaux = convertToEntity(animauxDto);
        animaux.setId(id);
        Animaux updated = animauxService.save(animaux);
        return ResponseEntity.ok(new AnimauxDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimaux(@PathVariable Long id) {
        if (!animauxService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        animauxService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Animaux convertToEntity(AnimauxDto dto) {
        Animaux animaux = new Animaux();
        animaux.setId(dto.getId());
        animaux.setEspeces(dto.getEspeces());
        animaux.setNombreTetes(dto.getNombreTetes());
        animaux.setModeAcquisition(dto.getModeAcquisition());
        animaux.setAnneeAcquisition(dto.getAnneeAcquisition());
        animaux.setValeurAcquisition(dto.getValeurAcquisition());
        animaux.setLocalite(dto.getLocalite());
        animaux.setDateCreation(dto.getDateCreation());
        animaux.setSynthese(dto.isSynthese());
        animaux.setIdDeclaration(dto.getIdDeclaration());
        return animaux;
    }
}