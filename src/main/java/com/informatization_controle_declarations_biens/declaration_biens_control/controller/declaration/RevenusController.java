package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.RevenusDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Revenus;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IRevenusService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.RevenusProjection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.util.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.IOException;
@RestController
@RequestMapping("/api/revenus")
public class RevenusController {

    private final IRevenusService revenusService;

    public RevenusController(IRevenusService revenusService) {
        this.revenusService = revenusService;
    }
    @PostMapping(value = "/upload/{revenusId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable Long revenusId,
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
    
        // Vérification du revenu
        Revenus revenus = revenusService.findById(revenusId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Revenu ID " + revenusId + " introuvable"));
    
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
    
            // Mise à jour du revenu
            revenus.setFileName(fileName);
            revenus.setFileType(contentType);
            revenus.setFileData(fileData);
    
            // Sauvegarde
            Revenus updated = revenusService.save(revenus);
    
            // Construction de l'URL de téléchargement
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/revenus/download/")
                    .path(revenusId.toString())
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
@GetMapping("/download/{revenusId}")
public ResponseEntity<Resource> downloadFile(@PathVariable Long revenusId) {
    Revenus revenus = revenusService.findById(revenusId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Revenus ID " + revenusId + " introuvable"));

    if (revenus.getFileData() == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Aucun fichier associé à ce revenu");
    }

    // Construction de la réponse
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(revenus.getFileType()))
            .contentLength(revenus.getFileData().length)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + revenus.getFileName() + "\"")
            .body(new ByteArrayResource(revenus.getFileData()));
}
// Dans RevenusController.java
@GetMapping("/by-autres-revenus/{autresRevenusId}")
public ResponseEntity<List<RevenusDto>> getByAutresRevenus(@PathVariable Long autresRevenusId) {
    List<RevenusProjection> projections = revenusService.findByAutresRevenus(autresRevenusId);
    List<RevenusDto> dtos = projections.stream()
            .map(RevenusDto::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}
    @GetMapping
    public ResponseEntity<List<RevenusDto>> getAllRevenus() {
        List<Revenus> list = revenusService.findAll();
        List<RevenusDto> dtos = list.stream()
                .map(RevenusDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<RevenusDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<RevenusProjection> projections = revenusService.getByDeclaration(declarationId);
        List<RevenusDto> dtos = projections.stream()
                .map(RevenusDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<RevenusDto> getRevenusById(@PathVariable Long id) {
        return revenusService.findById(id)
                .map(RevenusDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RevenusDto> createRevenus(@RequestBody RevenusDto dto) {
        Revenus entity = convertToEntity(dto);
        Revenus saved = revenusService.save(entity);
        return ResponseEntity.ok(new RevenusDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RevenusDto> updateRevenus(@PathVariable Long id, @RequestBody RevenusDto dto) {
        if (!revenusService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Revenus entity = convertToEntity(dto);
        entity.setId(id);
        Revenus updated = revenusService.save(entity);
        return ResponseEntity.ok(new RevenusDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRevenus(@PathVariable Long id) {
        if (!revenusService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        revenusService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Revenus convertToEntity(RevenusDto dto) {
        Revenus entity = new Revenus();
        entity.setId(dto.getId());
        entity.setAutresRevenus(dto.getAutresRevenus()); 
        entity.setDateCreation(dto.getDateCreation());
        entity.setIdDeclaration(dto.getIdDeclaration());
        entity.setFileName(dto.getFileName());
        entity.setFileType(dto.getFileType());
        entity.setFileData(dto.getFileData());
        return entity;
    }
    
}
