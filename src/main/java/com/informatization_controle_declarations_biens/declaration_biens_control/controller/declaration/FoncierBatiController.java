package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.FoncierBatiDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.PredictionResult;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IFoncierBatiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierBatiProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.parametrage.ParametrageService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/foncier-bati")
public class FoncierBatiController {

    private static final Logger logger = LoggerFactory.getLogger(FoncierBatiController.class);
    private final IFoncierBatiService service;
    private final ParametrageService parametrageService;
    @GetMapping("/by-nature/{natureId}")
public ResponseEntity<List<FoncierBatiDto>> getByNature(@PathVariable Long natureId) {
    List<FoncierBati> list = service.findByNatureId(natureId);
    List<FoncierBatiDto> dtos = list.stream()
            .map(FoncierBatiDto::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}

    // Restrict to common document formats only
    private final Set<String> ALLOWED_FILE_EXTENSIONS = new HashSet<>(Arrays.asList("pdf", "jpg", "jpeg", "png"));
    private final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(Arrays.asList(
        "application/pdf", 
        "image/jpeg", 
        "image/jpg", 
        "image/png"
    ));

    public FoncierBatiController(IFoncierBatiService service, ParametrageService parametrageService) {
        this.service = service;
        this.parametrageService = parametrageService;
    }

    @PostMapping(value = "/upload/{foncierId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable Long foncierId,
            @RequestPart("file") MultipartFile file) {
    
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le fichier ne peut pas être vide");
        }
    
        if (file.getSize() > 5 * 1024 * 1024) { // Reduced to 5MB for safety
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La taille du fichier ne doit pas dépasser 5MB");
        }
    
        FoncierBati foncier = service.findById(foncierId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID " + foncierId + " introuvable"));
    
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            
            // Validate file has an extension
            if (fileName == null || !fileName.contains(".")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Nom de fichier invalide. Le fichier doit avoir une extension.");
            }
            
            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            String contentType = file.getContentType();
            
            logger.info("Attempting to upload file: {}, type: {}, extension: {}", fileName, contentType, fileExtension);
            
            // Strict validation for file type
            if (!ALLOWED_FILE_EXTENSIONS.contains(fileExtension)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Extension de fichier non supportée. Extensions acceptées: pdf, jpg, jpeg, png");
            }
            
            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Type de fichier non supporté. Types acceptés: application/pdf, image/jpeg, image/jpg, image/png");
            }
            
            // Read file data with error handling
            byte[] fileData;
            try (InputStream inputStream = file.getInputStream()) {
                fileData = IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                logger.error("Error reading file data", e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Erreur lors de la lecture du fichier: " + e.getMessage());
            }
            
            // Validate file content
            if (fileData.length == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Le fichier est vide ou n'a pas pu être lu correctement");
            }
            
            // Additional validation for PDF files
            if ("pdf".equals(fileExtension) && !isPdfFileSignatureValid(fileData)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Le fichier n'est pas un PDF valide");
            }
            
            // Update entity with file data
            foncier.setFileName(fileName);
            foncier.setFileType(contentType);
            foncier.setFileData(fileData);
    
            FoncierBati updated = service.save(foncier);
            logger.info("Successfully saved file for foncierId: {}", foncierId);
    
            String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/foncier-bati/download/")
                    .path(foncierId.toString())
                    .toUriString();
    
            return ResponseEntity.ok().body(new FileUploadResponse(
                    fileName,
                    downloadUri,
                    foncier.getFileType(),
                    fileData.length
            ));
    
        } catch (ResponseStatusException e) {
            throw e; // Rethrow existing response exceptions
        } catch (Exception ex) {
            logger.error("Error processing file upload", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors du traitement du fichier: " + ex.getMessage());
        }
    }

    // Simple PDF signature validation - check for PDF header
    private boolean isPdfFileSignatureValid(byte[] data) {
        if (data.length < 5) return false;
        return data[0] == '%' && data[1] == 'P' && data[2] == 'D' && data[3] == 'F' && data[4] == '-';
    }

    @GetMapping("/download/{foncierId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long foncierId) {
        Optional<FoncierBati> foncierOpt = service.findById(foncierId);
        if (foncierOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        FoncierBati foncier = foncierOpt.get();
        
        // Validate file data exists
        if (foncier.getFileData() == null || foncier.getFileData().length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Document introuvable ou vide pour le foncier " + foncierId);
        }
        
        // Set default content type if none exists
        String contentType = foncier.getFileType() != null ? 
                            foncier.getFileType() : "application/octet-stream";
        
        String fileName = foncier.getFileName() != null ?
                          foncier.getFileName() : "document_" + foncierId;
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=\"" + fileName + "\"")
                .body(new ByteArrayResource(foncier.getFileData()));
    }

    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<FoncierBatiDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<FoncierBatiProjection> projections = service.getByDeclaration(declarationId);
        List<FoncierBatiDto> dtos = projections.stream()
                .map(FoncierBatiDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<FoncierBatiDto>> getAll() {
        try {
            List<FoncierBati> list = service.findAll();
            List<FoncierBatiDto> dtos = list.stream()
                    .map(entity -> {
                        try {
                            return new FoncierBatiDto(entity);
                        } catch (Exception e) {
                            logger.error("Error mapping entity to DTO: {}", entity.getId(), e);
                            return null;
                        }
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error fetching all foncier bati records", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erreur lors de la récupération des données: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoncierBatiDto> getById(@PathVariable Long id) {
        try {
            return service.findById(id)
                    .map(FoncierBatiDto::new)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching foncier bati with ID: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erreur lors de la récupération des données: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<FoncierBatiDto> create(@RequestBody FoncierBatiDto dto) {
        try {
            FoncierBati entity = convertToEntity(dto);
            FoncierBati saved = service.save(entity);
            return ResponseEntity.ok(new FoncierBatiDto(saved));
        } catch (Exception e) {
            logger.error("Error creating foncier bati", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erreur lors de la création: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoncierBatiDto> update(@PathVariable Long id, @RequestBody FoncierBatiDto dto) {
        try {
            if (!service.findById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            FoncierBati entity = convertToEntity(dto);
            entity.setId(id);
            
            // Preserve existing file data if not provided in the DTO
            if (dto.getFileData() == null) {
                service.findById(id).ifPresent(existing -> {
                    entity.setFileData(existing.getFileData());
                    entity.setFileName(existing.getFileName());
                    entity.setFileType(existing.getFileType());
                });
            }
            
            FoncierBati updated = service.save(entity);
            return ResponseEntity.ok(new FoncierBatiDto(updated));
        } catch (Exception e) {
            logger.error("Error updating foncier bati with ID: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            if (!service.findById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting foncier bati with ID: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erreur lors de la suppression: " + e.getMessage());
        }
    }

    private FoncierBati convertToEntity(FoncierBatiDto dto) {
        FoncierBati entity = new FoncierBati();
        entity.setId(dto.getId());
        entity.setNature(dto.getNature());
        entity.setAnneeConstruction(dto.getAnneeConstruction());
        entity.setModeAcquisition(dto.getModeAcquisition());
        entity.setReferencesCadastrales(dto.getReferencesCadastrales());
        entity.setSuperficie(dto.getSuperficie());
        entity.setLocalis(dto.getLocalis());
        entity.setTypeUsage(dto.getTypeUsage());
        entity.setCoutAcquisitionFCFA(dto.getCoutAcquisitionFCFA());
        entity.setCoutInvestissements(dto.getCoutInvestissements());
        entity.setDateCreation(dto.getDateCreation());
        entity.setSynthese(dto.isSynthese());
        entity.setIdDeclaration(dto.getIdDeclaration());
        
        // Only set file data if it's present in the DTO
        if (dto.getFileName() != null) {
            entity.setFileName(dto.getFileName());
            entity.setFileType(dto.getFileType());
        }
        if (dto.getFileData() != null) {
            entity.setFileData(dto.getFileData());
        }

        return entity;
    }


    /* // Nouvelle méthode pour récupérer la prédiction
    @GetMapping("/prediction/{id}")
    public ResponseEntity<Double> getPrediction(@PathVariable Long id) {
        FoncierBati foncierBati = service.findById(id).orElse(null);
        if (foncierBati == null) {
            return ResponseEntity.notFound().build();
        }
        // Appeler le service pour obtenir la prédiction
        double prediction = service.getPrediction(foncierBati);
        return ResponseEntity.ok(prediction);
    } */
    @GetMapping("/rapport-prediction/{declarationId}")
    public ResponseEntity<byte[]> generatePredictionReport(@PathVariable Long declarationId) {
        List<FoncierBati> list = service.getFullEntitiesByDeclaration(declarationId);
        List<PredictionResult> results = new ArrayList<>();
    
        for (FoncierBati foncier : list) {
            double prediction = service.getPrediction(foncier);
            results.add(new PredictionResult(foncier, prediction));
        }
    
        byte[] pdf = service.generatePdfRapport(results); // méthode à implémenter avec JasperReports ou autre
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_fraude.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    

}
