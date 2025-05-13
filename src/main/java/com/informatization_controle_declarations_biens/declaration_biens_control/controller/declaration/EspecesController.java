package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.EspecesDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Especes;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IEspecesService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EspecesProjection;
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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/especes")
public class EspecesController {

    private final IEspecesService service;

    public EspecesController(IEspecesService service) {
        this.service = service;
    }
    @PostMapping(value = "/upload/{especeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable Long especeId,
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
        Especes espece = service.findById(especeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Espèce ID " + especeId + " introuvable"));
    
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
            espece.setFileName(fileName);
            espece.setFileType(contentType);
            espece.setFileData(fileData);
    
            // Sauvegarde
            Especes updated = service.save(espece);
    
            // Construction de l'URL de téléchargement
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/especes/download/")
                    .path(especeId.toString())
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
    
    // La méthode downloadFile existe déjà, voici une version améliorée avec ResponseStatusException
    @GetMapping("/download/{especeId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long especeId) {
        Especes espece = service.findById(especeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Especes ID " + especeId + " introuvable"));
    
        if (espece.getFileData() == null || espece.getFileName() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aucun fichier associé à cette espèce");
        }
    
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(espece.getFileType()))
                .contentLength(espece.getFileData().length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + espece.getFileName() + "\"")
                .body(new ByteArrayResource(espece.getFileData()));
    }

    // Dans EspecesController.java
@GetMapping("/by-monnaie/{monnaie}")
public ResponseEntity<List<EspecesDto>> getByMonnaie(@PathVariable Float monnaie) {
    List<EspecesProjection> projections = service.findByMonnaie(monnaie);
    List<EspecesDto> dtos = projections.stream()
            .map(EspecesDto::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}
    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<EspecesDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<EspecesProjection> projections = service.getByDeclaration(declarationId);
        List<EspecesDto> dtos = projections.stream()
                .map(EspecesDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<EspecesDto>> getAll() {
        List<Especes> list = service.findAll();
        List<EspecesDto> dtos = list.stream()
                .map(EspecesDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecesDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(EspecesDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EspecesDto> create(@RequestBody EspecesDto dto) {
        Especes entity = convertToEntity(dto);
        Especes saved = service.save(entity);
        return ResponseEntity.ok(new EspecesDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecesDto> update(@PathVariable Long id, @RequestBody EspecesDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Especes entity = convertToEntity(dto);
        entity.setId(id);
        Especes updated = service.save(entity);
        return ResponseEntity.ok(new EspecesDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Especes convertToEntity(EspecesDto dto) {
        Especes entity = new Especes();
        entity.setId(dto.getId());
        entity.setMonnaie(dto.getMonnaie());
        entity.setDevise(dto.getDevise());
        entity.setTauxChange(dto.getTauxChange());
        entity.setMontantFCFA(dto.getMontantFCFA());
        entity.setMontantTotalFCFA(dto.getMontantTotalFCFA());
        entity.setDateEspece(dto.getDateEspece());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese());
        entity.setIdDeclaration(dto.getIdDeclaration());
        entity.setFileName(dto.getFileName());
        entity.setFileType(dto.getFileType());
        entity.setFileData(dto.getFileData());
        return entity;
    }
}
