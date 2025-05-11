package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.CommentaireGenerique;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.TypeEntiteEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

@Service
public class PdfRapportService {

    private final TemplateEngine templateEngine;
    private final CommentaireGeneriqueService commentaireService;  // <<== J'ajoute ici


    public PdfRapportService(CommentaireGeneriqueService commentaireService) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        this.templateEngine = new TemplateEngine();
        this.commentaireService = commentaireService;

        this.templateEngine.setTemplateResolver(templateResolver);
    }

    public void generateRapportEvaluationPdf(DeclarationDto declarationDto, RapportInfos rapportInfos, String filePath) {
        try {
            String processedHtml = fillRapportTemplate(declarationDto, rapportInfos);

            try (OutputStream outputStream = new FileOutputStream(filePath)) {
                ConverterProperties converterProperties = new ConverterProperties();

                File resourcesDir = new File("src/main/resources/static/");
                String baseUri = resourcesDir.getAbsolutePath() + File.separator;
                converterProperties.setBaseUri(baseUri);

                PdfWriter pdfWriter = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                pdfDocument.setDefaultPageSize(PageSize.A4);

                HtmlConverter.convertToPdf(processedHtml, pdfDocument, converterProperties);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport PDF", e);
        }
    }

    public static class RapportInfos {

        private String numeroOrdonnance;
        private Date dateOrdonnance;
        private String nomRapporteur;

        public RapportInfos(String numeroOrdonnance, Date dateOrdonnance, String nomRapporteur) {
            this.numeroOrdonnance = numeroOrdonnance;
            this.dateOrdonnance = dateOrdonnance;
            this.nomRapporteur = nomRapporteur;
        }

        public String getNumeroOrdonnance() { return numeroOrdonnance; }
        public Date getDateOrdonnance() { return dateOrdonnance; }
        public String getNomRapporteur() { return nomRapporteur; }
    }

    private String formatDate(Object date) {
        if (date == null) {
            return "Date non spécifiée";
        }

        if (date instanceof Date) {
            // Si c'est une java.util.Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            return dateFormat.format((Date) date);
        } else if (date instanceof LocalDate) {
            // Si c'est une java.time.LocalDate
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            return ((LocalDate) date).format(dateFormat);
        } else {
            // Si c'est un type inattendu
            throw new IllegalArgumentException("Type de date non supporté");
        }
    }

    private String fillRapportTemplate(DeclarationDto declarationDto, RapportInfos rapportInfos) {
        Context context = new Context();
    
        Assujetti assujetti = declarationDto.getAssujetti();
        Utilisateur user = declarationDto.getUtilisateur();
        Long declarationId = declarationDto.getId();
    
        String nomComplet = assujetti.getPrenom() + " " + assujetti.getNom();
        String rapporteur = user.getFirstname() + " " + user.getLastname();

        String dateDeclaration = formatDate(declarationDto.getDateDeclaration());
        String dateOrdonnance = formatDate(rapportInfos.getDateOrdonnance());
    
        context.setVariable("dateDeclaration", dateDeclaration);
        context.setVariable("civilite", assujetti.getCivilite().getIntitule());
        context.setVariable("nomComplet", nomComplet.toUpperCase());
        context.setVariable("typeDeclaration", declarationDto.getTypeDeclaration());
        context.setVariable("fonction", assujetti.getFonction().getIntitule()); // Décommenter cette ligne
        context.setVariable("numeroOrdonnance", rapportInfos.getNumeroOrdonnance());
        context.setVariable("dateOrdonnance", dateOrdonnance);
        context.setVariable("rapporteur", rapporteur);
        context.setVariable("introduction", getIntroductionText());
        context.setVariable("rappelsPreliminaires", getRappelsPreliminairesText());
        context.setVariable("methodologieControle", getMethodologieControle());
        context.setVariable("etapesControle", getEtapesControle());
        
        
        // Ajouter une variable pour la section de revue des biens déclarés
        context.setVariable("revueBiensDeclarés", "La revue des biens déclarés a été effectuée conformément aux dispositions légales en vigueur.");
    
        // Add vehicules
        context.setVariable("vehicules", declarationDto.getVehicules());
        
        // Add revenus
        context.setVariable("revenus", declarationDto.getRevenus());
        
        // Add fonciers batis
        context.setVariable("fonciersBatis", declarationDto.getFonciersBatis());
        
        // Add fonciers non batis
        context.setVariable("fonciersNonBatis", declarationDto.getFonciersNonBatis());
        
        // Add other sections as needed
        context.setVariable("emprunts", declarationDto.getEmprunts());
        context.setVariable("disponibilitesBanque", declarationDto.getDisponibilitesBanque());
        context.setVariable("especes", declarationDto.getEspeces());
        context.setVariable("animaux", declarationDto.getAnimaux());
        context.setVariable("meublesMeublants", declarationDto.getMeublesMeublants());
        context.setVariable("titres", declarationDto.getTitres());
        context.setVariable("creances", declarationDto.getCreances());
        context.setVariable("autresDettes", declarationDto.getAutresDettes());
        context.setVariable("autresBiensDeValeur", declarationDto.getAutresBiensDeValeur());
        context.setVariable("appareilsElectromenagers", declarationDto.getAppareilsElectromenagers());


        context.setVariable("commentairesVehicules", getCommentaires(declarationId, TypeEntiteEnum.Vehicule));
        context.setVariable("commentairesRevenus", getCommentaires(declarationId, TypeEntiteEnum.Revenus));
        context.setVariable("commentairesFonciersBatis", getCommentaires(declarationId, TypeEntiteEnum.FoncierBati));
        context.setVariable("commentairesFonciersNonBatis", getCommentaires(declarationId, TypeEntiteEnum.FoncierNonBati));
        context.setVariable("commentairesEmprunts", getCommentaires(declarationId, TypeEntiteEnum.Emprunts));
        context.setVariable("commentairesDisponibilitesBanque", getCommentaires(declarationId, TypeEntiteEnum.DisponibilitesEnBanque));
        context.setVariable("commentairesEspeces", getCommentaires(declarationId, TypeEntiteEnum.Especes));
        context.setVariable("commentairesAnimaux", getCommentaires(declarationId, TypeEntiteEnum.Animaux));
        context.setVariable("commentairesMeublesMeublants", getCommentaires(declarationId, TypeEntiteEnum.MeublesMeublants));
        context.setVariable("commentairesTitres", getCommentaires(declarationId, TypeEntiteEnum.Titres));
        context.setVariable("commentairesCreances", getCommentaires(declarationId, TypeEntiteEnum.LesCreances));
        context.setVariable("commentairesAutresDettes", getCommentaires(declarationId, TypeEntiteEnum.AutresDettes));
        context.setVariable("commentairesAutresBiensDeValeur", getCommentaires(declarationId, TypeEntiteEnum.AutresBiensDeValeur));
        context.setVariable("commentairesAppareilsElectromenagers", getCommentaires(declarationId, TypeEntiteEnum.AppareilsElectroMenagers));
        
        return templateEngine.process("Rapport_template", context);
    }
    private List<CommentaireGenerique> getCommentaires(Long declarationId, TypeEntiteEnum typeEntite) {
        return commentaireService.getCommentairesParDeclarationEtType(declarationId, typeEntite);
    }

    // Helper methods for formatting
    private String formatMoney(Float amount) {
        return amount != null ? String.format("%,.2f FCFA", amount) : "N/A";
    }
    
    private String getVocabValue(Vocabulaire vocab) {
        return (vocab != null) ? vocab.getIntitule() : "N/A";
    }

    private String getIntroductionText() {

        return """
       Le présent rapport sert de cadre de communication avec l’assujetti.
       Il est d’abord pédagogique car propice pour les notifications des rappels préliminaires.
       Ensuite par souci de transparence inhérente aux Institutions Supérieures de Contrôle des finances publiques (ISC),
       la déclinaison de la méthodologie y est détaillée à l’intention de l’assujetti. Au demeurant,
       il s’agit ainsi de la mise en œuvre de normes internationales des institutions supérieures de contrôle des finances publiques (ISSAI) qui doivent présider la conduite de certains contrôles dont le mandat est donné à la Cour des comptes.
       C’est enfin le cadre où se prépare le contrôle notamment par : 
       -\tDes demandes des compléments de renseignements les cas échéants ;
       -\tL’évaluation du patrimoine déclaré dans le respect du principe du contradictoire
       """;

    }

    private String getRappelsPreliminairesText() {

        return """
               RAPPELS PRELIMINAIRES (LEGISLATION)
    
               1. L’obligation de dépôt de la déclaration des biens :
               C’est une obligation prévue par la constitution et les autres textes subséquents. 
               Ce dépôt doit être volontaire et spontané par l’assujetti. Il n’est donc point besoin de rappeler à l’assujetti 
               de respecter une obligation qui lui incombe. Il en est de même pour l’obligation relative à la mise à jour annuelle 
               qui doit intervenir dans le mois suivant la date anniversaire du dépôt de la déclaration initiale. 
               C’est le premier pas de l’acceptation de la culture de l’intégrité prônée par la loi.
    
               2. Les infractions y rattachées :
               Le refus de se soumettre à cette obligation ouvre la voie aux investigations prévues par l’article 143 
               de la loi organique n° 2020-035 du 30 juillet 2020 déterminant les attributions, la composition, l’organisation 
               et le fonctionnement de la Cour des comptes. Le dépôt de la déclaration en retard expose l’assujetti à une amende de 
               50.000 F CFA par jour de retard.
               Article 79 de la Constitution : « Toute déclaration des biens inexacte ou mensongère expose son auteur à des poursuites 
               du chef de faux conformément aux dispositions du Code pénal ».
    
               3. La déclaration sur l’honneur :
               La déclaration sur l’honneur implique une présomption de bonne foi.
    
               4. La publication de la déclaration :
               La déclaration de biens doit être publiée au Journal Officiel et par voie de presse conformément aux prescriptions 
               de la Constitution du 25 novembre 2010 et de la loi organique sur la Cour des comptes.
    
               5. Précautions relatives aux données personnelles :
               La Cour des comptes a pris des précautions pour préserver les assujettis de désagréments pouvant découler 
               d’actes malveillants sur leurs données personnelles.
               """;
    }

    private String getMethodologieControle() {

        return """
               1. La publicité est un pilier du système d’intégrité recherché ; de ce fait, outre la publication in extenso de la déclaration 
               au journal officiel et par voie de presse, la synthèse des contrôles fait également l’objet de publication sans préjudice 
               des insertions au rapport général public. Elle permet d’informer l’opinion publique de la consistance du patrimoine déclaré 
               aux fins de l’exercice de la veille citoyenne pour éventuellement faire l’écho des violations du code d’honneur par les assujettis.
    
               2. Le patrimoine déclaré est évalué en espèces de la monnaie en cours par le magistrat instructeur, contradictoirement lors des échanges notamment.
    
               3. L’instruction, une fois terminée, est délibérée par un collège de trois (3) magistrats qui statue sur le respect de la légalité 
               en assurant également un contrôle qualité. Il est alors dressé un rapport définitif.
    
               4. La formation collégiale statue sur la valeur du patrimoine, arrête le montant et en donne acte à l’assujetti, jusqu’à plus ample informé. 
               Les termes « jusqu’à plus ample informé » contenus dans le dispositif impliquent la possibilité de la survenance de faits nouveaux, 
               dénoncés par toutes voies, et de nature à faire reconsidérer l’opinion de la Cour. Ils permettent une reprise du dossier aux fins de droit 
               à l’initiative du parquet général.
    
               5. La formation collégiale se prononce sur la licéité du patrimoine fondée sur la bonne foi de l’assujetti, lorsque celle-ci 
               n’est pas mise en doute par toutes voies, notamment la veille citoyenne.
               """;
    }
    private String getEtapesControle() {

        return """
        Le contrôle s’effectue dans l’ordre suivant :
            I.	Le respect du délai, du modèle et du contenu de la déclaration des biens institué par le décret no 2021-856/PRN/MJ du 07 octobre 2021
            II.	La revue des biens déclarés
            III.	L’évaluation du patrimoine estimé par le déclarant
            IV.	Transmission du rapport au Procureur Général 
            V.	Transmission du rapport provisoire à l’assujetti
        
        """;


    }
    

    


    
    
}
