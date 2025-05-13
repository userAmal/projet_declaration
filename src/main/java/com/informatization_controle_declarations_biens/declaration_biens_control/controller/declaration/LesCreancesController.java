package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.LesCreancesDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.LesCreances;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.ILesCreancesService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.LesCreancesProjection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;

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

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/les-creances")
public class LesCreancesController {

    private final ILesCreancesService service;

    public LesCreancesController(ILesCreancesService service) {
        this.service = service;
    }
    // Dans LesCreancesController.java
@GetMapping("/by-debiteur/{debiteurId}")
public ResponseEntity<List<LesCreancesDto>> getByDebiteur(@PathVariable Long debiteurId) {
    List<LesCreancesProjection> projections = service.findByDebiteur(debiteurId);
    List<LesCreancesDto> dtos = projections.stream()
            .map(LesCreancesDto::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}
    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {
    
        // Constantes de validation
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
    
        // Vérification de l'entité
        LesCreances creance = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Créance ID " + id + " introuvable"));
    
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
            byte[] fileData = file.getBytes();
            if (fileData == null || fileData.length == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Le fichier est vide ou corrompu");
            }
    
            // Mise à jour de l'entité
            creance.setFileName(fileName);
            creance.setFileType(contentType);
            creance.setFileData(fileData);
    
            // Sauvegarde
            LesCreances updated = service.save(creance);
    
            // Construction de l'URL de téléchargement
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/les-creances/download/")
                    .path(id.toString())
                    .toUriString();
    
            return ResponseEntity.ok().body(new FileUploadResponse(
                    fileName,
                    downloadUri,
                    contentType,
                    fileData.length
            ));
    
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors du traitement du fichier: " + ex.getMessage());
        }
    }
@GetMapping("/download/{id}")
public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
    LesCreances creance = service.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Créance ID " + id + " introuvable"));

    if (creance.getFileData() == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun fichier associé à cette créance");
    }

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(creance.getFileType()))
            .contentLength(creance.getFileData().length)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + creance.getFileName() + "\"")
            .body(new ByteArrayResource(creance.getFileData()));
}

    @GetMapping
    public ResponseEntity<List<LesCreancesDto>> getAll() {
        List<LesCreances> list = service.findAll();
        List<LesCreancesDto> dtos = list.stream()
                .map(LesCreancesDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<LesCreancesDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<LesCreancesProjection> projections = service.getByDeclaration(declarationId);
        List<LesCreancesDto> dtos = projections.stream()
                .map(LesCreancesDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LesCreancesDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(LesCreancesDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LesCreancesDto> create(@RequestBody LesCreancesDto dto) {
        LesCreances entity = convertToEntity(dto);
        LesCreances saved = service.save(entity);
        return ResponseEntity.ok(new LesCreancesDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LesCreancesDto> update(@PathVariable Long id, @RequestBody LesCreancesDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        LesCreances entity = convertToEntity(dto);
        entity.setId(id);
        LesCreances updated = service.save(entity);
        return ResponseEntity.ok(new LesCreancesDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private LesCreances convertToEntity(LesCreancesDto dto) {
        LesCreances entity = new LesCreances();
        entity.setId(dto.getId());
        entity.setMontant(dto.getMontant());
        entity.setAutresPrecisions(dto.getAutresPrecisions());
        entity.setDebiteurs(dto.getDebiteurs());
        entity.setDateCreation(dto.getDateCreation());
        entity.setIdDeclaration(dto.getIdDeclaration());
        entity.setFileName(dto.getFileName());
        entity.setFileType(dto.getFileType());
        entity.setFileData(dto.getFileData());
        return entity;
    }
}
