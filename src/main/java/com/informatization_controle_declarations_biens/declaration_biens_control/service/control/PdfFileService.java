package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.*;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.*;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
@Service
public class PdfFileService {

    private final TemplateEngine templateEngine;

    public PdfFileService() {
        // Configure Thymeleaf template engine
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
    }

   // Dans votre classe PdfFileService, modifiez la méthode generateFullDeclarationPdf 
// pour gérer correctement les ressources d'images

public void generateFullDeclarationPdf(DeclarationDto dto, String filePath) {
    try {
        // Fill template with data
        String processedHtml = fillTemplate(dto);
        
        // Convert HTML to PDF
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            ConverterProperties converterProperties = new ConverterProperties();
            
            // Définir le chemin de base pour les ressources
            // Utilisez le chemin absolu du dossier des ressources statiques
            File resourcesDir = new File("src/main/resources/static/");
            String baseUri = resourcesDir.getAbsolutePath() + File.separator;
            converterProperties.setBaseUri(baseUri);
            
            // Create PDF with landscape orientation
            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
            
            // Convert HTML to PDF
            HtmlConverter.convertToPdf(processedHtml, pdfDocument, converterProperties);
        }
    } catch (Exception e) {
        throw new RuntimeException("Erreur lors de la génération du PDF", e);
    }
}
    private String fillTemplate(DeclarationDto dto) {
        // Create Thymeleaf context
        Context context = new Context();
        
        // Add logo image reference
        context.setVariable("logoImage", "logoniger.jpg");
        
        // Add main declaration info
        context.setVariable("dateDeclaration", dto.getDateDeclaration());
        context.setVariable("typeDeclaration", dto.getTypeDeclaration());
        
        // Add assujetti info
        if (dto.getAssujetti() != null) {
            Assujetti assujetti = dto.getAssujetti();
            context.setVariable("nom", assujetti.getNom());
            context.setVariable("prenom", assujetti.getPrenom());
            context.setVariable("civilite", assujetti.getCivilite() != null ? assujetti.getCivilite().getIntitule() : "N/A");
            context.setVariable("contacttel", assujetti.getContacttel());
            context.setVariable("email", assujetti.getEmail());
            context.setVariable("code", assujetti.getCode());
            context.setVariable("etat", assujetti.getEtat().toString());
            context.setVariable("institution", assujetti.getInstitutions().getIntitule());
            context.setVariable("administration", assujetti.getAdministration().getIntitule());
            context.setVariable("entite", assujetti.getEntite().getIntitule());
            context.setVariable("fonction", assujetti.getFonction().getIntitule());
            context.setVariable("matricule", assujetti.getMatricule());
            
            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            context.setVariable("datePriseDeService", dateFormat.format(assujetti.getDatePriseDeService()));
        }
        
        // Add vehicules
        context.setVariable("vehicules", dto.getVehicules());
        
        // Add revenus
        context.setVariable("revenus", dto.getRevenus());
        
        // Add fonciers batis
        context.setVariable("fonciersBatis", dto.getFonciersBatis());
        
        // Add fonciers non batis
        context.setVariable("fonciersNonBatis", dto.getFonciersNonBatis());
        
        // Add other sections as needed
        context.setVariable("emprunts", dto.getEmprunts());
        context.setVariable("disponibilitesBanque", dto.getDisponibilitesBanque());
        context.setVariable("especes", dto.getEspeces());
        context.setVariable("animaux", dto.getAnimaux());
        context.setVariable("meublesMeublants", dto.getMeublesMeublants());
        context.setVariable("titres", dto.getTitres());
        context.setVariable("creances", dto.getCreances());
        context.setVariable("autresDettes", dto.getAutresDettes());
        context.setVariable("autresBiensDeValeur", dto.getAutresBiensDeValeur());
        context.setVariable("appareilsElectromenagers", dto.getAppareilsElectromenagers());
        
        // Process template with context
        return templateEngine.process("declaration-template", context);
    }
    
    // Helper methods for formatting
    private String formatMoney(Float amount) {
        return amount != null ? String.format("%,.2f FCFA", amount) : "N/A";
    }
    
    private String getVocabValue(Vocabulaire vocab) {
        return (vocab != null) ? vocab.getIntitule() : "N/A";
    }
}