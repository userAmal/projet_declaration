package com.informatization_controle_declarations_biens.declaration_biens_control.controller.control;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.informatization_controle_declarations_biens.declaration_biens_control.service.control.PdfFileService;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/reports")
public class PdfReportController {

    @Autowired
    private PdfFileService pdfFileService;

    @GetMapping("/all")
    public ResponseEntity<Resource> generateAllDeclarationsReport() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "declarations_" + timestamp + ".pdf";
            String filePath = "E:\\pfe_projet\\declaration\\rapport\\" + fileName;
            
            // Génère le rapport contenant toutes les déclarations
            pdfFileService.pdfCreation();
            
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/declaration/{id}")
    public ResponseEntity<Resource> generateDeclarationReport(@PathVariable Long id) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "declaration_" + id + "_" + timestamp + ".pdf";
            String filePath = "E:\\pfe_projet\\declaration\\rapport\\" + fileName;
            
            // Génère le rapport pour une déclaration spécifique
            pdfFileService.generateDeclarationPdf(id, filePath);
            
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}