package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.AutresBiensDeValeurDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresBiensDeValeur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAutresBiensDeValeurService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresBiensDeValeurProjection;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.Optional;


@RestController
@RequestMapping("/api/autres-biens-de-valeur")
public class AutresBiensDeValeurController {

    private final IAutresBiensDeValeurService service;

    public AutresBiensDeValeurController(IAutresBiensDeValeurService service) {
        this.service = service;
    }
    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {
    
        // Constantes de validation
        final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
        // Validation de base
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le fichier ne peut pas être vide");
        }
    
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "La taille du fichier ne doit pas dépasser " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB");
        }
    
        // Vérification de l'entité
        AutresBiensDeValeur entity = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Bien de valeur ID " + id + " introuvable"));
    
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            
            // Validation du nom de fichier
            if (fileName == null || fileName.contains("..")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Nom de fichier invalide: " + fileName);
            }
    
            // Validation de l'extension
            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Type de fichier non supporté. Formats acceptés: " + String.join(", ", ALLOWED_EXTENSIONS));
            }
    
            // Lecture du fichier
            byte[] fileData = file.getBytes();
            if (fileData == null || fileData.length == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Le fichier est vide ou corrompu");
            }
    
            // Mise à jour de l'entité
            entity.setFileName(fileName);
            entity.setFileType(file.getContentType());
            entity.setFileData(fileData);
    
            // Sauvegarde
            service.save(entity);
    
            // Construction de l'URL de téléchargement
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/autres-biens-de-valeur/download/")
                    .path(id.toString())
                    .toUriString();
    
            return ResponseEntity.ok().body(new FileUploadResponse(
                    fileName,
                    downloadUri,
                    file.getContentType(),
                    fileData.length
            ));
    
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors du traitement du fichier: " + ex.getMessage());
        }
    }
    
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        AutresBiensDeValeur entity = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "ID " + id + " introuvable"));
    
        if (entity.getFileData() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aucun fichier associé");
        }
    
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(entity.getFileType()))
                .contentLength(entity.getFileData().length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + entity.getFileName() + "\"")
                .body(new ByteArrayResource(entity.getFileData()));
    }
    // Dans AutresBiensDeValeurController.java
@GetMapping("/by-designation/{designationId}")
public ResponseEntity<List<AutresBiensDeValeurDto>> getByDesignation(@PathVariable Long designationId) {
    List<AutresBiensDeValeurProjection> projections = service.findByDesignation(designationId);
    List<AutresBiensDeValeurDto> dtos = projections.stream()
            .map(AutresBiensDeValeurDto::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}
    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<AutresBiensDeValeurDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<AutresBiensDeValeurProjection> projections = service.getByDeclaration(declarationId);
        List<AutresBiensDeValeurDto> dtos = projections.stream()
                .map(AutresBiensDeValeurDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

   
    @GetMapping("/{id}")
    public ResponseEntity<AutresBiensDeValeurDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(AutresBiensDeValeurDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AutresBiensDeValeurDto> create(@RequestBody AutresBiensDeValeurDto dto) {
        AutresBiensDeValeur entity = convertToEntity(dto);
        AutresBiensDeValeur saved = service.save(entity);
        return ResponseEntity.ok(new AutresBiensDeValeurDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutresBiensDeValeurDto> update(@PathVariable Long id, @RequestBody AutresBiensDeValeurDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        AutresBiensDeValeur entity = convertToEntity(dto);
        entity.setId(id);
        AutresBiensDeValeur updated = service.save(entity);
        return ResponseEntity.ok(new AutresBiensDeValeurDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    private AutresBiensDeValeur convertToEntity(AutresBiensDeValeurDto dto) {
        AutresBiensDeValeur entity = new AutresBiensDeValeur();
        entity.setId(dto.getId());
        entity.setDesignation(dto.getDesignation());
        entity.setLocalite(dto.getLocalite());
        entity.setAnneeAcquis(dto.getAnneeAcquis());
        entity.setValeurAcquisition(dto.getValeurAcquisition());
        entity.setAutrePrecisions(dto.getAutrePrecisions());
        entity.setType(dto.getType());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese());
            if (dto.getIdDeclaration() != null) {
            entity.setIdDeclaration(dto.getIdDeclaration());
        } else {
            throw new IllegalArgumentException("La déclaration est obligatoire");
        }
        entity.setFileName(dto.getFileName());
        entity.setFileType(dto.getFileType());
        entity.setFileData(dto.getFileData());
        return entity;
    }
    
}
