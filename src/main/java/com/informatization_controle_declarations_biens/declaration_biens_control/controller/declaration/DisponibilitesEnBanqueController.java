package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.DisponibilitesEnBanque;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDisponibilitesEnBanqueService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.DisponibilitesEnBanqueProjection;

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

@RestController
@RequestMapping("/api/disponibilites-en-banque")
public class DisponibilitesEnBanqueController {

    private final IDisponibilitesEnBanqueService service;

    public DisponibilitesEnBanqueController(IDisponibilitesEnBanqueService service) {
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
        DisponibilitesEnBanque entity = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Disponibilité en banque ID " + id + " introuvable"));
    
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
            DisponibilitesEnBanque updated = service.save(entity);
    
            // Construction de l'URL de téléchargement
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/disponibilites-en-banque/download/")
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
        DisponibilitesEnBanque entity = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID " + id + " introuvable"));
    
        if (entity.getFileData() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun fichier associé");
        }
    
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(entity.getFileType()))
                .contentLength(entity.getFileData().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + entity.getFileName() + "\"")
                .body(new ByteArrayResource(entity.getFileData()));
    }
    // Dans DisponibilitesEnBanqueController.java
@GetMapping("/by-banque/{banqueId}")
public ResponseEntity<List<DisponibilitesEnBanqueProjection>> getByBanque(@PathVariable Long banqueId) {
    List<DisponibilitesEnBanqueProjection> disponibilites = service.findByBanque(banqueId);
    return ResponseEntity.ok(disponibilites);
}
    @GetMapping
    public List<DisponibilitesEnBanque> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilitesEnBanque> getById(@PathVariable Long id) {
        Optional<DisponibilitesEnBanque> entity = service.findById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-declaration/{declarationId}")
    public List<DisponibilitesEnBanqueProjection> getByDeclaration(@PathVariable Long declarationId) {
        return service.getByDeclaration(declarationId);
    }

    @PostMapping
    public DisponibilitesEnBanque create(@RequestBody DisponibilitesEnBanque entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilitesEnBanque> update(@PathVariable Long id, @RequestBody DisponibilitesEnBanque entity) {
        if (service.findById(id).isPresent()) {
            return ResponseEntity.ok(service.save(entity));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
