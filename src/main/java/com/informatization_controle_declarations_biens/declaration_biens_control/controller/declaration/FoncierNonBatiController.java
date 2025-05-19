package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.FoncierBatiDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.FoncierNonBatiDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.PredictionResult;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierNonBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IFoncierNonBatiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.payload.FileUploadResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierNonBatiProjection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/foncier-non-bati")
public class FoncierNonBatiController {
    private static final Logger logger = LoggerFactory.getLogger(FoncierNonBatiController.class);

    private final IFoncierNonBatiService foncierNonBatiService;

    public FoncierNonBatiController(IFoncierNonBatiService foncierNonBatiService) {
        this.foncierNonBatiService = foncierNonBatiService;
    }
  // Constantes pour la validation des fichiers
  private static final List<String> ALLOWED_FILE_EXTENSIONS = Arrays.asList("pdf", "jpg", "jpeg", "png");
  private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
          "application/pdf", 
          "image/jpeg", 
          "image/jpg", 
          "image/png"
  );
  @GetMapping("/by-natureFNB/{natureId}")
public ResponseEntity<List<FoncierNonBatiDto>> getByNature(@PathVariable Long natureId) {
    List<FoncierNonBati> list = foncierNonBatiService.findByNatureId(natureId);
    List<FoncierNonBatiDto> dtos = list.stream()
            .map(FoncierNonBatiDto::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}
  @PostMapping(value = "/upload/{foncierId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileUploadResponse> uploadFile(
          @PathVariable Long foncierId,
          @RequestPart("file") MultipartFile file) {

      if (file == null || file.isEmpty()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le fichier ne peut pas être vide");
      }

      if (file.getSize() > 5 * 1024 * 1024) { // Réduit à 5MB pour la sécurité
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La taille du fichier ne doit pas dépasser 5MB");
      }

      FoncierNonBati foncier = foncierNonBatiService.findById(foncierId)
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID " + foncierId + " introuvable"));

      try {
          String fileName = StringUtils.cleanPath(file.getOriginalFilename());
          
          // Validation de l'extension du fichier
          if (fileName == null || !fileName.contains(".")) {
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                  "Nom de fichier invalide. Le fichier doit avoir une extension.");
          }
          
          String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
          String contentType = file.getContentType();
          
          logger.info("Tentative d'upload du fichier: {}, type: {}, extension: {}", fileName, contentType, fileExtension);
          
          // Validation stricte du type de fichier
          if (!ALLOWED_FILE_EXTENSIONS.contains(fileExtension)) {
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                  "Extension de fichier non supportée. Extensions acceptées: pdf, jpg, jpeg, png");
          }
          
          if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                  "Type de fichier non supporté. Types acceptés: application/pdf, image/jpeg, image/jpg, image/png");
          }
          
          // Lecture des données du fichier avec gestion des erreurs
          byte[] fileData;
          try (InputStream inputStream = file.getInputStream()) {
              fileData = IOUtils.toByteArray(inputStream);
          } catch (IOException e) {
              logger.error("Erreur lors de la lecture du fichier", e);
              throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                  "Erreur lors de la lecture du fichier: " + e.getMessage());
          }
          
          // Validation du contenu du fichier
          if (fileData.length == 0) {
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                  "Le fichier est vide ou n'a pas pu être lu correctement");
          }
          
          // Validation supplémentaire pour les fichiers PDF
          if ("pdf".equals(fileExtension) && !isPdfFileSignatureValid(fileData)) {
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                  "Le fichier n'est pas un PDF valide");
          }
          
          // Mise à jour de l'entité avec les données du fichier
          foncier.setFileName(fileName);
          foncier.setFileType(contentType);
          foncier.setFileData(fileData);

          FoncierNonBati updated = foncierNonBatiService.save(foncier);
          logger.info("Fichier enregistré avec succès pour foncierId: {}", foncierId);

          String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                  .path("/api/foncier-non-bati/download/")
                  .path(foncierId.toString())
                  .toUriString();

          return ResponseEntity.ok().body(new FileUploadResponse(
                  fileName,
                  downloadUri,
                  foncier.getFileType(),
                  fileData.length
          ));

      } catch (ResponseStatusException e) {
          throw e; // Relance les exceptions de réponse existantes
      } catch (Exception ex) {
          logger.error("Erreur lors du traitement de l'upload du fichier", ex);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                  "Erreur lors du traitement du fichier: " + ex.getMessage());
      }
  }

  // Méthode pour valider la signature d'un fichier PDF
  private boolean isPdfFileSignatureValid(byte[] data) {
      // Les premiers bytes d'un fichier PDF valide doivent être "%PDF"
      return data != null && data.length > 4 && 
             data[0] == 0x25 && // %
             data[1] == 0x50 && // P
             data[2] == 0x44 && // D
             data[3] == 0x46;   // F
  }
  
    @GetMapping("/download/{foncierId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long foncierId) {
        FoncierNonBati foncier = foncierNonBatiService.findById(foncierId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "FoncierNonBati ID " + foncierId + " introuvable"));
    
        if (foncier.getFileData() == null || foncier.getFileName() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aucun fichier associé à ce foncier non bâti");
        }
    
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(foncier.getFileType()))
                .contentLength(foncier.getFileData().length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + foncier.getFileName() + "\"")
                .body(new ByteArrayResource(foncier.getFileData()));
    }
    @GetMapping
    public ResponseEntity<List<FoncierNonBatiDto>> getAllFoncierNonBati() {
        List<FoncierNonBati> list = foncierNonBatiService.findAll();
        List<FoncierNonBatiDto> dtos = list.stream()
                .map(FoncierNonBatiDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/by-declaration/{declarationId}")
    public ResponseEntity<List<FoncierNonBatiDto>> getByDeclaration(@PathVariable Long declarationId) {
        List<FoncierNonBatiProjection> projections = foncierNonBatiService.getByDeclaration(declarationId);
        List<FoncierNonBatiDto> dtos = projections.stream()
                .map(FoncierNonBatiDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FoncierNonBatiDto> getFoncierNonBatiById(@PathVariable Long id) {
        return foncierNonBatiService.findById(id)
                .map(FoncierNonBatiDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FoncierNonBatiDto> createFoncierNonBati(@RequestBody FoncierNonBatiDto dto) {
        FoncierNonBati entity = convertToEntity(dto);
        FoncierNonBati saved = foncierNonBatiService.save(entity);
        return ResponseEntity.ok(new FoncierNonBatiDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoncierNonBatiDto> updateFoncierNonBati(@PathVariable Long id, @RequestBody FoncierNonBatiDto dto) {
        if (!foncierNonBatiService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        FoncierNonBati entity = convertToEntity(dto);
        entity.setId(id);
        FoncierNonBati updated = foncierNonBatiService.save(entity);
        return ResponseEntity.ok(new FoncierNonBatiDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoncierNonBati(@PathVariable Long id) {
        if (!foncierNonBatiService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        foncierNonBatiService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private FoncierNonBati convertToEntity(FoncierNonBatiDto dto) {
        FoncierNonBati entity = new FoncierNonBati();
        entity.setId(dto.getId());
        entity.setNature(dto.getNature()); 
        entity.setModeAcquisition(dto.getModeAcquisition()); 
        entity.setIlot(dto.getIlot());
        entity.setLotissement(dto.getLotissement());
        entity.setSuperficie(dto.getSuperficie());
        entity.setLocalite(dto.getLocalite());
        entity.setTitrePropriete(dto.getTitrePropriete());
        entity.setDateAcquis(dto.getDateAcquis());
        entity.setValeurAcquisFCFA(dto.getValeurAcquisFCFA());
        entity.setCoutInvestissements(dto.getCoutInvestissements());
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
        // 1. Get full entities
        List<FoncierNonBati> properties = foncierNonBatiService.getFullEntitiesByDeclaration(declarationId);
        
        // 2. Calculate predictions
        List<PredictionResult> results = properties.stream()
            .map(property -> {
                double prediction = foncierNonBatiService.getPrediction(property);
                return new PredictionResult(property, prediction);
            })
            .collect(Collectors.toList());
        
        // 3. Generate PDF
        byte[] pdf = foncierNonBatiService.generatePdfRapportNonBati(results);
        
        // 4. Return response
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_non_bati.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
    

