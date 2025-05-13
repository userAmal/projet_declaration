package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.AppareilsElectroMenagersDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AppareilsElectroMenagers;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAppareilsElectroMenagersService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AppareilsElectroMenagersProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.DeclarationService;

import jakarta.persistence.EntityNotFoundException;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;


import org.springframework.util.StringUtils;
@RestController
@RequestMapping("/api/appareils-electromenagers")
public class AppareilsElectroMenagersController {

    private final IAppareilsElectroMenagersService service;

    @Autowired
    private DeclarationService declarationRepository;

    public AppareilsElectroMenagersController(IAppareilsElectroMenagersService service) {
        this.service = service;
    }
    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {
    
        final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );
        final long MAX_FILE_SIZE = 10 * 1024 * 1024; 
    
        try {
            if (file == null || file.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le fichier ne peut pas être vide");
            }
    
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "La taille du fichier ne doit pas dépasser " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB");
            }
    
            AppareilsElectroMenagers appareil = service.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Appareil électroménager ID " + id + " introuvable"));
    
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            
            if (fileName == null || fileName.contains("..")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Nom de fichier invalide: " + fileName);
            }
    
            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Type de fichier non supporté. Formats acceptés: " + String.join(", ", ALLOWED_EXTENSIONS));
            }
    
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
            appareil.setFileName(fileName);
            appareil.setFileType(contentType);
            appareil.setFileData(fileData);
    
            // Sauvegarde
            AppareilsElectroMenagers updated = service.save(appareil);
    
            // Construction de l'URL de téléchargement
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/appareils-electromenagers/download/")
                    .path(id.toString())
                    .toUriString();
    
            // Construction de la réponse
            FileUploadResponse response = new FileUploadResponse(
                    fileName,
                    downloadUri,
                    contentType,
                    fileData.length
            );
    
            return ResponseEntity.ok(response);
    
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Resource not found");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (ResponseStatusException e) {
            throw e; // Re-lancer les exceptions de réponse déjà gérées
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "Une erreur est survenue lors du traitement du fichier");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        AppareilsElectroMenagers appareil = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "AppareilsElectroMenagers ID " + id + " introuvable"));
    
        if (appareil.getFileData() == null || appareil.getFileData().length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun fichier associé à cet appareil");
        }
    
        ByteArrayResource resource = new ByteArrayResource(appareil.getFileData());
    
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(appareil.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + appareil.getFileName() + "\"")
                .body(resource);
    }
    @GetMapping("/search")
public ResponseEntity<?> searchByDesignation(@RequestParam String designation) {
    try {
        List<AppareilsElectroMenagers> result = service.findByDesignation(designation);
        List<AppareilsElectroMenagersDto> dtos = result.stream()
                .map(AppareilsElectroMenagersDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    } catch (Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<?> getByDeclaration(@PathVariable Long declarationId) {
        try {
            // Vérifier si la déclaration existe
            Declaration declaration = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration with id " + declarationId + " not found"));
            
            List<AppareilsElectroMenagersProjection> projections = service.getByDeclaration(declarationId);
            List<AppareilsElectroMenagersDto> dtos = projections.stream()
                    .map(AppareilsElectroMenagersDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (EntityNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Resource not found");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Internal server error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<AppareilsElectroMenagersDto>> getAll() {
        List<AppareilsElectroMenagers> list = service.findAll();
        List<AppareilsElectroMenagersDto> dtos = list.stream()
                .map(AppareilsElectroMenagersDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            AppareilsElectroMenagers appareil = service.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("AppareilsElectroMenagers with id " + id + " not found"));
            return ResponseEntity.ok(new AppareilsElectroMenagersDto(appareil));
        } catch (EntityNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Resource not found");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Internal server error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AppareilsElectroMenagersDto dto) {
        try {
            // Vérifier si la déclaration existe
            Long declarationId = dto.getId();
            if (declarationId != null) {
                declarationRepository.findById(declarationId)
                    .orElseThrow(() -> new EntityNotFoundException("Declaration with id " + declarationId + " not found"));
            }
            
            AppareilsElectroMenagers entity = convertToEntity(dto);
            AppareilsElectroMenagers saved = service.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new AppareilsElectroMenagersDto(saved));
        } catch (EntityNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Resource not found");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Internal server error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AppareilsElectroMenagersDto dto) {
        try {
            // Vérifier si l'appareil existe
            service.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AppareilsElectroMenagers with id " + id + " not found"));
            
            // Vérifier si la déclaration existe
            Long declarationId = dto.getId();
            if (declarationId != null) {
                declarationRepository.findById(declarationId)
                    .orElseThrow(() -> new EntityNotFoundException("Declaration with id " + declarationId + " not found"));
            }
            
            AppareilsElectroMenagers entity = convertToEntity(dto);
            entity.setId(id);
            AppareilsElectroMenagers updated = service.save(entity);
            return ResponseEntity.ok(new AppareilsElectroMenagersDto(updated));
        } catch (EntityNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Resource not found");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Internal server error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            // Vérifier si l'appareil existe
            service.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AppareilsElectroMenagers with id " + id + " not found"));
            
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Resource not found");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Internal server error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private AppareilsElectroMenagers convertToEntity(AppareilsElectroMenagersDto dto) {
        AppareilsElectroMenagers entity = new AppareilsElectroMenagers();
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
        entity.setDesignation(dto.getDesignation());
        
        return entity;
    }
}