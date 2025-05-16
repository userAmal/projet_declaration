package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.IRapportData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle.IRapportService;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport.Type;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class RapportService implements IRapportService {

    @Autowired
    private IRapportData rapportData;
    @Autowired
    private  IDeclarationData declarationData;

    private final TemplateEngine templateEngine;

    public RapportService() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/rapports/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    public Rapport genererRapportProvisoire(Utilisateur utilisateur, Declaration declaration, String contenu) {
        Context context = prepareCommonContext(utilisateur, declaration);
        context.setVariable("contenu", contenu);
        
        String htmlContent = templateEngine.process("Rapport_template", context);
        byte[] pdfContent = genererPdf(htmlContent);
        
        String reference = generateReference(Rapport.Type.PROVISOIRE, declaration.getId());
        String nomFichier = generateFilename(Rapport.Type.PROVISOIRE, declaration.getId());
        
        return saveRapport(utilisateur, declaration, pdfContent, reference, nomFichier, Rapport.Type.PROVISOIRE, null);
    }

    @Override
    public Rapport genererRapportDefinitif(Utilisateur utilisateur, 
                                        Declaration declaration, 
                                        Boolean decision,
                                        String contenu) {
        try {
            Context context = new Context();
            
            context.setVariable("utilisateur", utilisateur);
            context.setVariable("declaration", declaration);
            context.setVariable("contenu", contenu);
            context.setVariable("decision", decision);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            context.setVariable("dateGeneration", LocalDateTime.now().format(formatter));
            
            String nomComplet = declaration.getAssujetti() != null ? 
                declaration.getAssujetti().getNom() + " " + declaration.getAssujetti().getPrenom() : "N/A";
            context.setVariable("nomComplet", nomComplet);

            String reference = generateReference(Type.DEFINITIF, declaration.getId());
            context.setVariable("reference", reference);
            
            // Créer un rapport temporaire pour le template
            Rapport rapport = new Rapport();
            rapport.setDateCreation(LocalDateTime.now());
            context.setVariable("rapport", rapport);
            
            String htmlContent = templateEngine.process("definitif-template", context);
            byte[] pdfContent = genererPdf(htmlContent);
            
            if (decision != null) {
                if (decision) {
                    declaration.setEtatDeclaration(EtatDeclarationEnum.valider);
                } else {
                    declaration.setEtatDeclaration(EtatDeclarationEnum.refuser);
                }
                declarationData.save(declaration); 
            }
            
            rapport = Rapport.builder()
                .type(Type.DEFINITIF)
                .declaration(declaration)
                .utilisateur(utilisateur)
                .dateCreation(LocalDateTime.now())
                .nomFichier(generateFilename(Type.DEFINITIF, declaration.getId()))
                .contenuPdf(pdfContent)
                .reference(reference)
                .tailleFichier(pdfContent.length)
                .decision(decision)
                .build();
            
            return rapportData.save(rapport);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport définitif", e);
        }
    }

    private Context prepareCommonContext(Utilisateur utilisateur, Declaration declaration) {
        Context context = new Context();
        context.setVariable("utilisateur", utilisateur);
        context.setVariable("declaration", declaration);
        context.setVariable("dateGeneration", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return context;
    }

    private String generateReference() {
        return "RDiff-" + LocalDateTime.now().getYear() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private byte[] genererPdf(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            HtmlConverter.convertToPdf(htmlContent, pdf, new ConverterProperties());
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private Rapport saveRapport(Utilisateur utilisateur, Declaration declaration, byte[] pdfContent, 
                              String reference, String nomFichier, Rapport.Type type, Boolean decision) {
        Rapport rapport = Rapport.builder()
            .type(type)
            .declaration(declaration)
            .utilisateur(utilisateur)
            .dateCreation(LocalDateTime.now())
            .nomFichier(nomFichier)
            .contenuPdf(pdfContent)
            .reference(reference)
            .tailleFichier(pdfContent.length)
            .decision(decision)
            .build();
        
        return rapportData.save(rapport);
    }

    private String generateReference(Rapport.Type type, Long declarationId) {
        return "RAPP-" + type + "-" + declarationId + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String generateFilename(Rapport.Type type, Long declarationId) {
        return type + "-" + declarationId + "-" + LocalDateTime.now().getYear() + ".pdf";
    }

    @Override
    public ResponseEntity<Resource> telechargerRapport(Long rapportId) {
        Rapport rapport = rapportData.findById(rapportId)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé"));

        ByteArrayResource resource = new ByteArrayResource(rapport.getContenuPdf());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + rapport.getNomFichier() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(rapport.getTailleFichier())
                .body(resource);
    }

    @Override
    public List<Rapport> getRapportsByDeclaration(Long declarationId) {
        return rapportData.findByDeclarationId(declarationId);
    }

    @Override
    public List<Rapport> getRapportsByUtilisateur(Long utilisateurId) {
        return rapportData.findByUtilisateurId(utilisateurId);
    }

    @Override
    public List<Rapport> getRapportsByType(Rapport.Type type) {
        return rapportData.findByType(type);
    }

    @Override
    public Rapport getRapportById(Long id) {
        return rapportData.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé"));
    }

    @Override
    public void deleteRapport(Long id) {
        if (!rapportData.existsById(id)) {
            throw new RuntimeException("Rapport non trouvé");
        }
        rapportData.deleteById(id);
    }
    
}