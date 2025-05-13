package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.AnimauxDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Animaux;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAnimauxService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AnimauxProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.parametrage.ParametrageService;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import org.springframework.util.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;






@RestController
@RequestMapping("/api/animaux")
public class AnimauxController {

    private final IAnimauxService animauxService;
    private final ParametrageService parametrageService;

    public AnimauxController(IAnimauxService animauxService, ParametrageService parametrageService) {
        this.animauxService = animauxService;
        this.parametrageService = parametrageService;

    }

    @GetMapping("/search-by-especes")
public ResponseEntity<List<Animaux>> searchByEspeces(@RequestParam String especes) {
    List<Animaux> result = animauxService.findByEspeces(especes);
    return ResponseEntity.ok(result);
}

    @PostMapping(value = "/upload/{animauxId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable Long animauxId,
            @RequestPart("file") MultipartFile file) {
    
        // Constantes pour la validation
        final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );
        final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
        // Validation de base
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le fichier ne peut pas être vide");
        }
    
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "La taille du fichier ne doit pas dépasser " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB");
        }
    
        // Vérification de l'existence de l'animal
        Animaux animaux = animauxService.findById(animauxId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Animaux ID " + animauxId + " introuvable"));
    
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            
            // Validation du nom de fichier
            if (fileName == null || fileName.contains("..")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Nom de fichier invalide: " + fileName);
            }
    
            // Extraction et validation de l'extension
            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Type de fichier non supporté. Formats acceptés: " + String.join(", ", ALLOWED_EXTENSIONS));
            }
    
            // Validation du type MIME
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Type MIME non supporté. Types acceptés: " + String.join(", ", ALLOWED_MIME_TYPES));
            }
    
            // Lecture sécurisée du fichier
            byte[] fileData;
            try {
                fileData = file.getBytes();
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erreur lors de la lecture du fichier: " + e.getMessage());
            }
    
            // Validation des données du fichier
            if (fileData == null || fileData.length == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Le fichier est vide ou corrompu");
            }
    
            // Mise à jour de l'entité
            animaux.setFileName(fileName);
            animaux.setFileType(contentType);
            animaux.setFileData(fileData);
    
            // Sauvegarde
            Animaux updated = animauxService.save(animaux);
    
            // Construction de l'URL de téléchargement
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/animaux/download/")
                    .path(animauxId.toString())
                    .toUriString();
    
            return ResponseEntity.ok().body(new FileUploadResponse(
                    fileName,
                    downloadUri,
                    contentType,
                    fileData.length
            ));
    
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors du traitement du fichier: " + ex.getMessage());
        }
    }
    @GetMapping("/download/{animauxId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long animauxId) {
        Animaux animaux = animauxService.findById(animauxId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Animaux ID " + animauxId + " introuvable"));
    
        if (animaux.getFileData() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aucun fichier associé à cet animal");
        }
    
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(animaux.getFileType()))
                .contentLength(animaux.getFileData().length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + animaux.getFileName() + "\"")
                .body(new ByteArrayResource(animaux.getFileData()));
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
        animaux.setFileName(dto.getFileName());
        animaux.setFileType(dto.getFileType());
        animaux.setFileData(dto.getFileData());
        return animaux;
    }
}