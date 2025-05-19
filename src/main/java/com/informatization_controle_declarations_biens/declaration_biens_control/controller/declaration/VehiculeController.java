package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.VehiculeDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.PredictionResult;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vehicule;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IVehiculeService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VehiculeProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.IOException;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final IVehiculeService service;

    public VehiculeController(IVehiculeService service) {
        this.service = service;
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
    
        // Vérification du véhicule
        Vehicule vehicule = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Véhicule ID " + id + " introuvable"));
    
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
    
            // Mise à jour du véhicule
            vehicule.setFileName(fileName);
            vehicule.setFileType(contentType);
            vehicule.setFileData(fileData);
    
            // Sauvegarde
            Vehicule updated = service.save(vehicule);
    
            // Construction de l'URL de téléchargement
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/vehicules/download/")
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
    Vehicule vehicule = service.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Véhicule ID " + id + " introuvable"));

    if (vehicule.getFileData() == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun fichier associé à ce véhicule");
    }

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(vehicule.getFileType()))
            .contentLength(vehicule.getFileData().length)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + vehicule.getFileName() + "\"")
            .body(new ByteArrayResource(vehicule.getFileData()));
}

    @GetMapping("/designation/{designationId}")
    public ResponseEntity<List<Vehicule>> getVehiculesByDesignation(@PathVariable Long designationId) {
        List<Vehicule> vehicules = service.findByDesignation(designationId);
        if (vehicules.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vehicules);
    }
    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<VehiculeDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<VehiculeProjection> projections = service.getByDeclaration(declarationId);
        List<VehiculeDto> dtos = projections.stream()
                .map(VehiculeDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<VehiculeDto>> getAll() {
        List<Vehicule> list = service.findAll();
        List<VehiculeDto> dtos = list.stream()
                .map(VehiculeDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(VehiculeDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VehiculeDto> create(@RequestBody VehiculeDto dto) {
        Vehicule entity = convertToEntity(dto);
        Vehicule saved = service.save(entity);
        return ResponseEntity.ok(new VehiculeDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeDto> update(@PathVariable Long id, @RequestBody VehiculeDto dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Vehicule entity = convertToEntity(dto);
        entity.setId(id);
        Vehicule updated = service.save(entity);
        return ResponseEntity.ok(new VehiculeDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Vehicule convertToEntity(VehiculeDto dto) {
        Vehicule entity = new Vehicule();
        entity.setId(dto.getId());
        entity.setDesignation(dto.getDesignation());
        entity.setMarque(dto.getMarque());
        entity.setImmatriculation(dto.getImmatriculation());
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

       @GetMapping("/rapport-prediction/{declarationId}")
public ResponseEntity<byte[]> generatePredictionReport(@PathVariable Long declarationId) {
    List<VehiculeProjection> projections = service.getByDeclaration(declarationId);
    List<PredictionResult> results = new ArrayList<>();
    
    for (VehiculeProjection projection : projections) {
        Vehicule vehicule = service.findById(projection.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Véhicule non trouvé"));
        double prediction = service.getPrediction(vehicule);
        results.add(new PredictionResult(vehicule, prediction));
    }
    
    byte[] pdf = service.generatePdfRapport(results);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_vehicules.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}
}
