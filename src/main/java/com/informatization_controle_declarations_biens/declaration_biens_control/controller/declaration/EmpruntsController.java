package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Emprunts;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IEmpruntsService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EmpruntsProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
@RequestMapping("/api/emprunts")
public class EmpruntsController {

    private final IEmpruntsService service;

    public EmpruntsController(IEmpruntsService service) {
        this.service = service;
    }
    @GetMapping("/by-institution/{vocabulaireId}")
public List<Emprunts> getByInstitution(@PathVariable Long vocabulaireId) {
    return service.getByInstitutionFinanciere(vocabulaireId);
}

    @PostMapping(value = "/upload/{empruntId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable Long empruntId,
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
        Emprunts emprunt = service.findById(empruntId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Emprunt ID " + empruntId + " introuvable"));
    
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
            emprunt.setFileName(fileName);
            emprunt.setFileType(file.getContentType());
            emprunt.setFileData(fileData);
    
            // Sauvegarde
            Emprunts updated = service.save(emprunt);
    
            // Construction de l'URL de téléchargement
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/emprunts/download/")
                    .path(empruntId.toString())
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
@GetMapping("/download/{empruntId}")
public ResponseEntity<Resource> downloadFile(@PathVariable Long empruntId) {
    Optional<Emprunts> empruntOpt = service.findById(empruntId);
    if (empruntOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    Emprunts emprunt = empruntOpt.get();
    
    // Vérifier si le fichier existe
    if (emprunt.getFileData() == null || emprunt.getFileName() == null) {
        return ResponseEntity.notFound().build();
    }
    
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(emprunt.getFileType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                   "attachment; filename=\"" + emprunt.getFileName() + "\"")
            .body(new ByteArrayResource(emprunt.getFileData()));
}
    @GetMapping
    public List<Emprunts> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emprunts> getById(@PathVariable Long id) {
        Optional<Emprunts> entity = service.findById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-declaration/{declarationId}")
    public List<EmpruntsProjection> getByDeclaration(@PathVariable Long declarationId) {
        return service.getByDeclaration(declarationId);
    }

    @PostMapping
    public Emprunts create(@RequestBody Emprunts entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Emprunts> update(@PathVariable Long id, @RequestBody Emprunts entity) {
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
