package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.TitresDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Titres;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.ITitresService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TitresProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
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
@RequestMapping("/api/titres")
public class TitresController {

    private final ITitresService titresService;

    public TitresController(ITitresService titresService) {
        this.titresService = titresService;
    }
    @PostMapping(value = "/upload/{titreId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable Long titreId,
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
    
        // Vérification du titre
        Titres titre = titresService.findById(titreId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Titre ID " + titreId + " introuvable"));
    
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
    
            // Mise à jour du titre
            titre.setFileName(fileName);
            titre.setFileType(contentType);
            titre.setFileData(fileData);
    
            // Sauvegarde
            Titres updated = titresService.save(titre);
    
            // Construction de l'URL de téléchargement
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/titres/download/")
                    .path(titreId.toString())
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
@GetMapping("/download/{titreId}")
public ResponseEntity<Resource> downloadFile(@PathVariable Long titreId) {
    Titres titre = titresService.findById(titreId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Titres ID " + titreId + " introuvable"));

    if (titre.getFileData() == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Aucun fichier associé à ce titre");
    }

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(titre.getFileType()))
            .contentLength(titre.getFileData().length)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + titre.getFileName() + "\"")
            .body(new ByteArrayResource(titre.getFileData()));
}
// Dans TitresController.java
@GetMapping("/by-designation/{designationId}")
public ResponseEntity<List<TitresDto>> getByDesignationNatureActions(@PathVariable Long designationId) {
    List<TitresProjection> projections = titresService.findByDesignationNatureActions(designationId);
    List<TitresDto> dtos = projections.stream()
            .map(TitresDto::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}
    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<TitresDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<TitresProjection> projections = titresService.getByDeclaration(declarationId);
        List<TitresDto> dtos = projections.stream()
                .map(TitresDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<TitresDto>> getAll() {
        List<Titres> list = titresService.findAll();
        List<TitresDto> dtos = list.stream()
                .map(TitresDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TitresDto> getById(@PathVariable Long id) {
        return titresService.findById(id)
                .map(TitresDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TitresDto> create(@RequestBody TitresDto dto) {
        Titres entity = convertToEntity(dto);
        Titres saved = titresService.save(entity);
        return ResponseEntity.ok(new TitresDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TitresDto> update(@PathVariable Long id, @RequestBody TitresDto dto) {
        if (!titresService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Titres entity = convertToEntity(dto);
        entity.setId(id);
        Titres updated = titresService.save(entity);
        return ResponseEntity.ok(new TitresDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!titresService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        titresService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Titres convertToEntity(TitresDto dto) {
        Titres entity = new Titres();
        entity.setId(dto.getId());
        entity.setDesignationNatureActions(dto.getDesignationNatureActions());
        entity.setValeurEmplacement(dto.getValeurEmplacement());
        entity.setEmplacement(dto.getEmplacement());
        entity.setAutrePrecisions(dto.getAutrePrecisions());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese());
        entity.setIdDeclaration(dto.getIdDeclaration());
        entity.setFileName(dto.getFileName());
        entity.setFileType(dto.getFileType());
        entity.setFileData(dto.getFileData());
        return entity;
    }
    
}
