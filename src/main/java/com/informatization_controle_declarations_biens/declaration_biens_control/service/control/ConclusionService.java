package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.IConclusionData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Conclusion;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle.IConclusionService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConclusionService implements IConclusionService {

    @Autowired
    private IConclusionData conclusionData;

    private final TemplateEngine templateEngine;

    public ConclusionService() {
        // Configuration du moteur de template Thymeleaf
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    public Conclusion genererConclusion(Declaration declaration, byte[] pdfContent, String fileName) {
        // Create and save the conclusion
        Conclusion conclusion = Conclusion.builder()
            .declaration(declaration)
            .dateCreation(LocalDateTime.now())
            .nomFichier(fileName)
            .contenuPdf(pdfContent)
            .reference(generateReference())
            .tailleFichier(pdfContent.length)
            .build();

        return conclusionData.save(conclusion);
    }

    @Override
    public Conclusion genererLettreOfficielle(Utilisateur utilisateur,
                                           Declaration declaration,
                                           String contenuUtilisateur) {
        // Générer le contenu PDF
        byte[] pdfContent = genererPdfLettre(utilisateur, declaration, contenuUtilisateur);

        // Créer et sauvegarder la conclusion
        String fileName = "lettre-" + generateReference() + ".pdf";
        Conclusion conclusion = Conclusion.builder()
            .declaration(declaration)
            .utilisateur(utilisateur)
            .dateCreation(LocalDateTime.now())
            .nomFichier(fileName)
            .contenuPdf(pdfContent)
            .reference(generateReference())
            .tailleFichier(pdfContent.length)
            .build();

        return conclusionData.save(conclusion);
    }

    private byte[] genererPdfLettre(Utilisateur utilisateur,
                                  Declaration declaration,
                                  String contenuUtilisateur) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Préparer le contexte Thymeleaf
            Context context = new Context();
            context.setVariable("utilisateur", utilisateur);
            context.setVariable("declaration", declaration);
            context.setVariable("reference", generateReference());
            
            // Format de date plus approprié pour l'affichage
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            context.setVariable("dateGeneration", LocalDateTime.now().format(formatter));
            
            // Récupérer le nom complet de l'assujetti depuis la déclaration
            String nomComplet = declaration.getAssujetti() != null ? 
                declaration.getAssujetti().getNom() + " " + declaration.getAssujetti().getPrenom() : "N/A";
            context.setVariable("nomComplet", nomComplet);
            
            // Date de la déclaration
            String dateDeclaration = declaration.getDateDeclaration() != null ? 
                declaration.getDateDeclaration().format(formatter) : "N/A";
            context.setVariable("dateDeclaration", dateDeclaration);
            
            context.setVariable("contenuUtilisateur", contenuUtilisateur);

            // Traiter le template
            String htmlContent = templateEngine.process("lettre-template", context);

            // Configuration de la conversion PDF
            ConverterProperties properties = new ConverterProperties();
            
            // Configurer le chemin de base pour les ressources
            properties.setBaseUri("classpath:/static/");
            
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            
            // Convertir HTML en PDF
            HtmlConverter.convertToPdf(htmlContent, pdf, properties);
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    @Override
    public ResponseEntity<Resource> telechargerConclusion(Long conclusionId) {
        Conclusion conclusion = conclusionData.findById(conclusionId)
                .orElseThrow(() -> new RuntimeException("Conclusion non trouvée"));

        ByteArrayResource resource = new ByteArrayResource(conclusion.getContenuPdf());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + conclusion.getNomFichier() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(conclusion.getTailleFichier())
                .body(resource);
    }

    @Override
    public List<Conclusion> getConclusionsByDeclaration(Long declarationId) {
        return conclusionData.findByDeclarationId(declarationId);
    }

    @Override
    public List<Conclusion> getConclusionsByUtilisateur(Long utilisateurId) {
        return conclusionData.findByUtilisateurId(utilisateurId);
    }

    private String generateReference() {
        return "CONCL-" + LocalDateTime.now().getYear() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    @Override
    public Optional<Conclusion> findById(Long id) {
        return conclusionData.findById(id);
    }

    @Override
    public void deleteConclusion(Long id) {
        if (!conclusionData.existsById(id)) {
            throw new RuntimeException("Conclusion non trouvée avec l'ID: " + id);
        }
        conclusionData.deleteById(id);
    }
    
    @Override
    public List<Conclusion> findByUtilisateurAndDeclaration(Long utilisateurId, Long declarationId) {
        return conclusionData.findByUtilisateurIdAndDeclarationId(utilisateurId, declarationId);
    }

    
}