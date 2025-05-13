package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.MeublesMeublantsDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.MeublesMeublants;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IMeublesMeublantsService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.MeublesMeublantsProjection;

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
@RequestMapping("/api/meubles-meublants")
public class MeublesMeublantsController {

    private final IMeublesMeublantsService service;

    public MeublesMeublantsController(IMeublesMeublantsService service) {
        this.service = service;
    }
    @GetMapping("/search")
public ResponseEntity<List<MeublesMeublantsDto>> searchByDesignation(@RequestParam String designation) {
    List<MeublesMeublants> meubles = service.searchByDesignation(designation);
    List<MeublesMeublantsDto> dtos = meubles.stream()
            .map(MeublesMeublantsDto::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}

    @PostMapping(value = "/upload/{meubleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable Long meubleId,
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
    
        // Vérification du meuble
        MeublesMeublants meuble = service.findById(meubleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Meuble ID " + meubleId + " introuvable"));
    
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
    
            // Mise à jour du meuble
            meuble.setFileName(fileName);
            meuble.setFileType(contentType);
            meuble.setFileData(fileData);
    
            // Sauvegarde
            MeublesMeublants updated = service.save(meuble);
    
            // Construction de l'URL de téléchargement
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/meubles-meublants/download/")
                    .path(meubleId.toString())
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
@GetMapping("/download/{meubleId}")
public ResponseEntity<Resource> downloadFile(@PathVariable Long meubleId) {
    MeublesMeublants meuble = service.findById(meubleId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Meuble ID " + meubleId + " introuvable"));

    if (meuble.getFileData() == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Aucun fichier associé à ce meuble");
    }

    // Construction de la réponse
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(meuble.getFileType()))
            .contentLength(meuble.getFileData().length)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + meuble.getFileName() + "\"")
            .body(new ByteArrayResource(meuble.getFileData()));
}
    @GetMapping
    public ResponseEntity<List<MeublesMeublantsDto>> getAll() {
        List<MeublesMeublants> list = service.findAll();
        List<MeublesMeublantsDto> dtos = list.stream()
                .map(MeublesMeublantsDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeublesMeublantsDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(MeublesMeublantsDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MeublesMeublantsDto> create(@RequestBody MeublesMeublantsDto dto) {
        MeublesMeublants entity = convertToEntity(dto);
        MeublesMeublants saved = service.save(entity);
        return ResponseEntity.ok(new MeublesMeublantsDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeublesMeublantsDto> update(@PathVariable Long id, @RequestBody MeublesMeublantsDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        MeublesMeublants entity = convertToEntity(dto);
        entity.setId(id);
        MeublesMeublants updated = service.save(entity);
        return ResponseEntity.ok(new MeublesMeublantsDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<MeublesMeublantsDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<MeublesMeublantsProjection> projections = service.getByDeclaration(declarationId);
        List<MeublesMeublantsDto> dtos = projections.stream()
                .map(MeublesMeublantsDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    private MeublesMeublants convertToEntity(MeublesMeublantsDto dto) {
        MeublesMeublants entity = new MeublesMeublants();
        entity.setId(dto.getId());
        entity.setDesignation(dto.getDesignation());
        entity.setAnneeAcquisition(dto.getAnneeAcquisition());
        entity.setValeurAcquisition(dto.getValeurAcquisition());
        entity.setEtatGeneral(dto.getEtatGeneral());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese());
        entity.setIdDeclaration(dto.getIdDeclaration());
        entity.setFileName(dto.getFileName());
        entity.setFileType(dto.getFileType());
        entity.setFileData(dto.getFileData());
        return entity;
    }
    
}
