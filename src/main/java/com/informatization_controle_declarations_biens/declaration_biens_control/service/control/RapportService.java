package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.IRapportData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle.IRapportService;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport.Type;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.TypeEntiteEnum;
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
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RapportService implements IRapportService {

    @Autowired
    private IRapportData rapportData;
    @Autowired
    private  IDeclarationData declarationData;
    private final CommentaireGeneriqueService commentaireService;  // <<== J'ajoute ici
    private final DeclarationDtoLoader declarationDtoLoader;
    private final TemplateEngine templateEngine;

    public RapportService(CommentaireGeneriqueService commentaireService, DeclarationDtoLoader declarationDtoLoader) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/rapports/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        this.commentaireService = commentaireService;
        this.declarationDtoLoader = declarationDtoLoader;        
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
    }

   @Override
public Rapport genererRapportProvisoire(Utilisateur utilisateur, Declaration declaration, String contenu) {
    // Chargez le DTO complet
    DeclarationDto declarationDto = declarationDtoLoader.loadFullDeclarationDto(declaration.getId());
    
    // Préparer le contexte
    Context context = prepareCommonContext(utilisateur, declarationDto);
    context.setVariable("contenu", contenu);
    
    // Générer le PDF
    String htmlContent = templateEngine.process("Rapport_template", context);
    byte[] pdfContent = genererPdf(htmlContent);
    
    // Enregistrer le rapport
    String reference = generateReference(Rapport.Type.PROVISOIRE, declaration.getId());
    String nomFichier = generateFilename(Rapport.Type.PROVISOIRE, declaration.getId());
    
    return saveRapport(utilisateur, declaration, pdfContent, reference, nomFichier, Rapport.Type.PROVISOIRE, null);
}




private Context prepareCommonContext(Utilisateur utilisateur, DeclarationDto declarationDto) {
    Context context = new Context();

    try {
        Resource resource = new ClassPathResource("static/logoniger.jpg");
        byte[] imageBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        context.setVariable("logoBase64", base64Image);
    } catch (IOException e) {
        // Gérer l'erreur ou utiliser un logo par défaut
       // logger.error("Erreur lors du chargement du logo", e);
        context.setVariable("logoBase64", ""); // ou une image par défaut encodée
    }
    
    Assujetti assujetti = declarationDto.getAssujetti();
    String nomComplet = assujetti.getPrenom() + " " + assujetti.getNom();
    String rapporteur = utilisateur.getFirstname() + " " + utilisateur.getLastname();

    String dateDeclaration = formatDate(declarationDto.getDateDeclaration());
    
    context.setVariable("dateDeclaration", dateDeclaration);
    context.setVariable("civilite", assujetti.getCivilite().getIntitule());
    context.setVariable("nomComplet", nomComplet.toUpperCase());
    context.setVariable("typeDeclaration", declarationDto.getTypeDeclaration());
    context.setVariable("fonction", assujetti.getFonction().getIntitule());
    context.setVariable("rapporteur", rapporteur);
    context.setVariable("dateGeneration", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    
    // Sections de texte
    context.setVariable("introduction", getIntroductionText());
    context.setVariable("rappelsPreliminaires", getRappelsPreliminairesText());
    context.setVariable("methodologieControle", getMethodologieControle());
    context.setVariable("etapesControle", getEtapesControle());
    context.setVariable("revueBiensDeclarés", "La revue des biens déclarés a été effectuée conformément aux dispositions légales en vigueur.");
    
    
    // Sections des biens déclarés
    context.setVariable("vehicules", declarationDto.getVehicules());
    context.setVariable("revenus", declarationDto.getRevenus());
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

        
        
    // Commentaires
    Long declarationId = declarationDto.getId();
    context.setVariable("commentairesVehicules", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.Vehicule));
    context.setVariable("commentairesRevenus", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.Revenus));
    context.setVariable("commentairesFonciersBatis", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.FoncierBati));
    context.setVariable("commentairesFonciersNonBatis", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.FoncierNonBati));
    context.setVariable("commentairesEmprunts", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.Emprunts));
    context.setVariable("commentairesDisponibilitesBanque", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.DisponibilitesEnBanque));
    context.setVariable("commentairesEspeces", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.Especes));
    context.setVariable("commentairesAnimaux", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.Animaux));
    context.setVariable("commentairesMeublesMeublants", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.MeublesMeublants));
    context.setVariable("commentairesTitres", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.Titres));
    context.setVariable("commentairesCreances", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.LesCreances));
    context.setVariable("commentairesAutresDettes", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.AutresDettes));
    context.setVariable("commentairesAutresBiensDeValeur", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.AutresBiensDeValeur));
    context.setVariable("commentairesAppareilsElectromenagers", commentaireService.getCommentairesParDeclarationEtType(declarationId, TypeEntiteEnum.AppareilsElectroMenagers));   
    
    
    context.setVariable("evaluationPatrimoine", prepareEvaluationPatrimoine(declarationDto));

    return context;
}

// Méthodes helpers à ajouter
private String formatDate(LocalDate date) {
    if (date == null) return "Date non spécifiée";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    return date.format(formatter);
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
        """;


    }

private Map<String, Object> prepareEvaluationPatrimoine(DeclarationDto declarationDto) {
    Map<String, Object> evaluationData = new HashMap<>();
    
    // Tableau des Actifs
    List<Map<String, Object>> actifs = new ArrayList<>();
    double totalActifs = 0.0;
    
    // Fonciers Bâtis
    if (declarationDto.getFonciersBatis() != null && !declarationDto.getFonciersBatis().isEmpty()) {
        double total = declarationDto.getFonciersBatis().stream()
            .mapToDouble(f -> f.getCoutAcquisitionFCFA() + f.getCoutInvestissements())
            .sum();
        actifs.add(createActifEntry("fonciersBatis", "Biens fonciers bâtis", total));
        totalActifs += total;
    }
    
    // Fonciers Non Bâtis
    if (declarationDto.getFonciersNonBatis() != null && !declarationDto.getFonciersNonBatis().isEmpty()) {
        double total = declarationDto.getFonciersNonBatis().stream()
            .mapToDouble(f -> f.getValeurAcquisFCFA() + f.getCoutInvestissements())
            .sum();
        actifs.add(createActifEntry("fonciersNonBatis", "Biens fonciers non bâtis", total));
        totalActifs += total;
    }
    
    // Véhicules
    if (declarationDto.getVehicules() != null && !declarationDto.getVehicules().isEmpty()) {
        double total = declarationDto.getVehicules().stream()
            .mapToDouble(v -> v.getValeurAcquisition())
            .sum();
        actifs.add(createActifEntry("vehicules", "Véhicules", total));
        totalActifs += total;
    }
    
    // Disponibilités Bancaires
    if (declarationDto.getDisponibilitesBanque() != null && !declarationDto.getDisponibilitesBanque().isEmpty()) {
        double total = declarationDto.getDisponibilitesBanque().stream()
            .mapToDouble(d -> d.getSoldeFCFA())
            .sum();
        actifs.add(createActifEntry("disponibilitesBanque", "Disponibilités bancaires", total));
        totalActifs += total;
    }
    
    // Espèces
    if (declarationDto.getEspeces() != null && !declarationDto.getEspeces().isEmpty()) {
        double total = declarationDto.getEspeces().stream()
            .mapToDouble(e -> e.getMontantTotalFCFA())
            .sum();
        actifs.add(createActifEntry("especes", "Espèces", total));
        totalActifs += total;
    }
    
    // Tableau des Passifs
    List<Map<String, Object>> passifs = new ArrayList<>();
    double totalPassifs = 0.0;
    double totalMontantPret = 0.0;  // Ajout pour suivre le total des montants des prêts
    
    // Emprunts
    if (declarationDto.getEmprunts() != null) {
        for (Object empruntObj : declarationDto.getEmprunts()) {
            try {
                Class<?> clazz = empruntObj.getClass();
                Float montantPret = (Float) clazz.getMethod("getMontantEmprunt").invoke(empruntObj);
                Float montantRestant = (Float) clazz.getMethod("getMontantRestant").invoke(empruntObj);
                LocalDate dateCreation = (LocalDate) clazz.getMethod("getDateCreation").invoke(empruntObj);

                // Debug: Afficher les valeurs récupérées
                System.out.println("Emprunt - MontantPret: " + montantPret 
                    + ", MontantRestant: " + montantRestant
                    + ", Date: " + dateCreation);

                passifs.add(createPassifEntry(
                    "Emprunt",
                    montantPret, // Montant initial du prêt
                    dateCreation != null ? dateCreation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                    montantRestant // Montant restant
                ));
                
                totalPassifs += montantRestant;
                totalMontantPret += montantPret;  // Ajouter au total des montants des prêts
                
            } catch (Exception e) {
                System.err.println("Erreur emprunt: " + e.getMessage());
                passifs.add(createPassifEntry(
                    "Emprunt (erreur)",
                    0f,
                    "N/A",
                    0f
                ));
            }
        }
    }
    
    // Autres Dettes (montant = montant restant)
    if (declarationDto.getAutresDettes() != null) {
        for (Object dette : declarationDto.getAutresDettes()) {
            try {
                Class<?> clazz = dette.getClass();
                Float montant = (Float) clazz.getMethod("getMontant").invoke(dette);
                LocalDate dateCreation = (LocalDate) clazz.getMethod("getDateCreation").invoke(dette);
                
                // Pour les dettes, montantPret = montant (car pas de distinction)
                passifs.add(createPassifEntry(
                    "Dette",
                    montant != null ? montant : 0f,
                    dateCreation != null ? dateCreation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                    montant != null ? montant : 0f
                ));
                
                float montantValue = montant != null ? montant : 0f;
                totalPassifs += montantValue;
                totalMontantPret += montantValue;  // Ajouter au total des montants des prêts
            } catch (Exception e) {
                System.err.println("Erreur dette: " + e.getMessage());
            }
        }
    }
    
    // Ajoutez la ligne de total pour les actifs
    actifs.add(createActifEntry("totalActifs", "TOTAL ACTIFS", totalActifs));
    
    // Ajoutez la ligne de total pour les passifs avec le montant total des prêts
    passifs.add(createPassifEntry("TOTAL PASSIFS", (float)totalMontantPret, "", totalPassifs));
    
    evaluationData.put("actifs", actifs);
    evaluationData.put("passifs", passifs);
    evaluationData.put("patrimoineNet", totalActifs - totalPassifs);
    
    return evaluationData;
}

private Map<String, Object> createActifEntry(String id, String nature, double montant) {
    Map<String, Object> entry = new HashMap<>();
    entry.put("id", id);
    entry.put("nature", nature);
    entry.put("montant", montant);
    return entry;
}

private Map<String, Object> createPassifEntry(String typeDette, float montantPret, String datePret, double montantRestant) {
    Map<String, Object> entry = new HashMap<>();
    entry.put("typeDette", typeDette);          // Clé exacte utilisée dans le template
    entry.put("montantPret", montantPret);      // Clé exacte
    entry.put("datePret", datePret);            // Clé exacte
    entry.put("montantRestant", montantRestant); // Clé exacte
    return entry;
}

// ... autres méthodes pour les textes prédéfinis

    @Override
    public Rapport genererRapportDefinitif(Utilisateur utilisateur, 
                                        Declaration declaration, 
                                        Boolean decision,
                                        String contenu) {


        
        try {
            Context context = new Context();
            
             DeclarationDto declarationDto = declarationDtoLoader.loadFullDeclarationDto(declaration.getId());
    
            context = prepareCommonContext(utilisateur, declarationDto);
            
            context.setVariable("utilisateur", utilisateur);
            context.setVariable("declaration", declaration);
            context.setVariable("contenu", contenu);
            context.setVariable("decision", decision);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            context.setVariable("dateGeneration", LocalDateTime.now().format(formatter));

            try {
        Resource resource = new ClassPathResource("static/logoniger.jpg");
        byte[] imageBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        context.setVariable("logoBase64", base64Image);
        } catch (IOException e) {
            // Gérer l'erreur ou utiliser un logo par défaut
        // logger.error("Erreur lors du chargement du logo", e);
            context.setVariable("logoBase64", ""); // ou une image par défaut encodée
        }

            // Prepare DELIBERE text
            String delibereText = prepareDelibereText(declaration, utilisateur);
            context.setVariable("delibereText", delibereText);

             // Ajoutez l'introduction au contexte
            String introductionText = prepareIntroductionText(declaration);
            context.setVariable("introduction", introductionText);

             // Ajoutez l'introduction au contexte
            String RAPPELSPRELIMINAIRES = RAPPELSPRELIMINAIRES(declaration);
            context.setVariable("rappelspreliminaires", RAPPELSPRELIMINAIRES);

             // Ajoutez l'introduction au contexte
            String PRINCIPESDUCONTROLEETMETHODOLOGIE = PRINCIPESDUCONTROLEETMETHODOLOGIE(declaration);
            context.setVariable("principesducontroleetmethodologie", PRINCIPESDUCONTROLEETMETHODOLOGIE);

            // Ajoutez l'introduction au contexte
            String ETAPESDUCONTROLE = ETAPESDUCONTROLE(declaration);
            context.setVariable("etapesducontrole", ETAPESDUCONTROLE);
                
            // Ajoutez l'introduction au contexte
            String respectdudélai = respectdudélai(declaration);
            context.setVariable("respectdudélai", respectdudélai);
                
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

    

private String getFrenchDeclarationType(TypeDeclarationEnum type) {
    switch (type) {
        case Initiale:
            return "initiale";
        case Mise_à_jour:
            return "de mise à jour";
        default:
            return "de type " + type.name().toLowerCase();
    }
}

    private String prepareIntroductionText(Declaration declaration) {

        String civilite = declaration.getAssujetti() != null &&
                      declaration.getAssujetti().getCivilite() != null ?
                      declaration.getAssujetti().getCivilite().getIntitule() + " " : "";

        String nomComplet = declaration.getAssujetti() != null ?
                       (declaration.getAssujetti().getPrenom() != null ?
                        declaration.getAssujetti().getPrenom() + " " : "") +
                       (declaration.getAssujetti().getNom() != null ?
                        declaration.getAssujetti().getNom().toUpperCase() : "NOM INCONNU") :
                       "ASSUJETTI INCONNU";

        String fonction = declaration.getAssujetti() != null &&
                      declaration.getAssujetti().getFonction() != null ?
                      declaration.getAssujetti().getFonction().getIntitule() :
                      "FONCTION INCONNUE";

        String typeDeclaration = declaration.getTypeDeclaration() != null ?
                             getFrenchDeclarationType(declaration.getTypeDeclaration()) :
                             "de type inconnu";
    return """
        
        La Constitution du 25 novembre 2010 a institué le contrôle des déclarations sur l'honneur des biens du Président de la République, 
        du Premier ministre, des Ministres, des Présidents des institutions de la République et des Responsables des autorités administratives indépendantes.
        
        Par la suite, d’autres assujettis au contrôle se sont ajoutés par le truchement de dispositions législatives subséquentes à savoir : 
            -	la loi n° 2014-07 du 16 avril 2014 portant adoption du Code de transparence dans la gestion des finances publiques au sein de l’Union Economique et Monétaire Ouest Africaine (UEMOA) et qui transpose la directive communautaire de ladite institution. 
            -	la loi n° 2020-028 du 02 juillet 2020 déterminant les autres agents publics assujettis à l'obligation de déclaration des biens. 

        Enfin, la loi organique n° 2020-035 du 30 juillet 2020 déterminant les attributions, la composition, l’organisation et le fonctionnement de la Cour des comptes a parachevé le dispositif juridique en lui attribuant le mandat de procéder à ce contrôle et en organisant la procédure y afférente. 
        
        La quatrième chambre compétente en matière de discipline budgétaire et financière et contrôle des comptes des partis politiques est chargée de ce contrôle. Ce contrôle doit être perçu comme une PREVENTION à l’enrichissement illicite, infraction assimilée à la corruption, dont les normes internationales des institutions de contrôle des finances publiques de l’Organisation Internationale des Institutions Supérieures de Contrôle des Finances Publiques (INTOSAI), à travers les lignes directrices relatives à la bonne gouvernance pour les biens publics (GUID 5260) et celles portant sur l’audit de la prévention de la corruption (GUID 5270) en ont fait une préoccupation. 
        
        Le contrôle de la déclaration des biens s’inscrit également dans l’esprit des Normes Internationales des Institutions Supérieures de contrôle des finances publiques (International Standards of Supreme Audit Institutions - ISSAI 12) sur la valeur et les avantages des institutions supérieures de contrôle des finances publiques – faire une différence dans la vie des citoyens, qui énonce que les Institutions Supérieures de Contrôle des finances publiques (ISC) devront, en vertu de leurs mandats, répondre de manière appropriée aux risques d’irrégularités financières, de fraude et de corruption.

        L’objectif principal de ce contrôle est de promouvoir l’intégrité des acteurs des finances publiques, élus ou hauts fonctionnaires. En effet, la loi no 2014-07 du 16 avril 2014 portant adoption du Code de transparence dans la gestion des finances publiques au sein de l’UEMOA prévoit dans son préambule que « Les acteurs publics qui pilotent et gèrent les fonds publics, élus ou fonctionnaires acceptent des obligations d'intégrité et de rectitude particulièrement exigeant, à mesure de la confiance qui leur est faite.  Ils sont également tenus à ce devoir pour prévenir tout risque d’enrichissement illicite.
       
        Au sens de l’ordonnance n° 92-024 du 18 juin 1992 portant répression de l'enrichissement illicite, « le délit d'enrichissement illicite est constitué lorsqu'il est établi qu'une personne possède un patrimoine et/ou mène un train de vie que ses revenus licites ne lui permettent pas de justifier ».

        Le présent rapport décrit donc le processus du contrôle opéré suite à la transmission de la déclaration %s des biens de %s%s, 
        au Ministère des finances, en sa qualité de %s suivant le décret n° 2021-438/PRN/MF du 10 juin 2021.

        À l'issue du contrôle, mené dans le respect des normes internationales, une opinion sur l'intégrité de l'assujetti est émise 
        en rapport avec le respect de ses obligations et la consécration de son honorabilité, jusqu'à plus ample informé.
        """.formatted(
            typeDeclaration,
            civilite,
            nomComplet,
            fonction
        );
}

private String RAPPELSPRELIMINAIRES(Declaration declaration) {

    return """
        
        1.	L’obligation de dépôt de la déclaration des biens : elle est prévue par la constitution et les autres textes subséquents. Ce dépôt doit être volontaire et spontané par l’assujetti. Il n’est donc point besoin de rappeler à l’assujetti de respecter une obligation qui lui incombe. C’est le premier pas de l’acceptation de la culture de l’intégrité prônée par la loi.
        2.	Les infractions y rattachées : le refus de se soumettre à cette obligation ouvre la voie aux investigations prévues par l’art 143 de la loi organique n° 2020-035 du 30 juillet 2020 déterminant les attributions, la composition, l’organisation et le fonctionnement de la Cour des comptes. 
        Le dépôt de la déclaration en retard expose l’assujetti à une amende de 50.000 frs par jour de retard.
        Art 79 de la Constitution : « Toute déclaration des biens inexacte ou mensongère expose son auteur à des poursuites du chef de faux conformément aux dispositions du Code pénal ».         
        3.	La déclaration sur l’honneur : elle implique une présomption de bonne foi. 
        4.	La publication de la déclaration : la déclaration de biens doit être publiée au journal officiel et par voie de presse conformément aux prescriptions de la constitution du 25 novembre 2010 et de la loi organique sur la Cour des comptes. 
        5.	Précautions relatives aux données personnelles : la Cour des comptes a pris des précautions pour préserver les assujettis de désagréments pouvant découler d’actes malveillants sur leurs données personnelles.

        """;
}

private String ETAPESDUCONTROLE(Declaration declaration) {

    return """
        
        Le contrôle s’effectue dans l’ordre suivant :
            I.	Le respect du délai, du modèle et du contenu de la déclaration des biens institué par le décret no 2021-856/PRN/MJ du 07 octobre 2021
            II.	L’évaluation des biens estimés par le déclarant
            III. Le dispositif 
        """;
}

private String respectdudélai(Declaration declaration) {
    
    String civilite = declaration.getAssujetti() != null &&
                      declaration.getAssujetti().getCivilite() != null ?
                      declaration.getAssujetti().getCivilite().getIntitule() + " " : "";

        String nomComplet = declaration.getAssujetti() != null ?
                       (declaration.getAssujetti().getPrenom() != null ?
                        declaration.getAssujetti().getPrenom() + " " : "") +
                       (declaration.getAssujetti().getNom() != null ?
                        declaration.getAssujetti().getNom().toUpperCase() : "NOM INCONNU") :
                       "ASSUJETTI INCONNU";

    return """
        
        En application des dispositions combinées des articles 78 et 139 de la loi organique régissant la Cour des comptes, le Premier Ministre et les Ministres doivent remettre dans les sept (7) jours de leur entrée en fonction au Premier Président de la Cour des comptes la déclaration écrite sur l’honneur de leurs biens. Cette déclaration fait l’objet d’une mise à jour annuelle et à la cessation de fonction. Ces dispositions s’étendent aux Président des Institutions de la République, aux responsables des autorités administratives indépendantes et à tout autre agent public soumis à la déclaration des biens.
        En outre, la loi n° 2020-028 du 02 juillet 2020 déterminant les autres agents publics assujettis à l’obligation de déclaration des biens et son décret no 2021-856/PRN/MJ du 07 octobre 2021 fixant le modèle et le contenu n’ont pas précisé le délai de dépôt desdites déclarations ; mais, la note de service n° 353 du 30 novembre 2021 du Premier Président de la Cour des comptes rend opposable aux tiers cette déclaration à partir de cette date.
        %s%s, Directeur des Systèmes d'Information à la Direction Générale des Douanes au Ministère des finances, ayant pris service le 13 juillet 2021 a transmis à la Cour la déclaration initiale de ses biens le 21 avril 2022. 
        La déclaration transmise respecte le modèle et le contenu tels qu’institués par le décret précité. 


        """.formatted(
            civilite,
            nomComplet
        );
}

private String PRINCIPESDUCONTROLEETMETHODOLOGIE(Declaration declaration) {

    return """
        
        	⮚	Le mandat de la Cour (ISSAI 20 et 100) : La Cour, en tant que plus haute juridiction de contrôle des finances publiques, est chargée du contrôle des déclarations des biens conformément à la loi organique la régissant et également à la loi no 2014-07 du 16 avril 2014 portant adoption du Code de transparence dans la gestion des finances publiques au sein de l’UEMOA. Ce dernière a circoncis ce contrôle au domaine des finances publiques, pendant le temps de l’occupation d’une fonction de haut fonctionnaire ou d’élu, et poursuit l’objectif de la promotion de la culture de l’intégrité en matière budgétaire. 
            ⮚	La déontologie de la Cour (ISSAI 130) :  Le contrôle des déclarations des biens est mené en conformité à l’ISSAI 130 relative au code de déontologie dans les institutions supérieures de contrôle.
            ⮚	Les principes du contrôle :
                ✔	La publicité (ISSAI 21) :
                La publicité est un pilier du système d’intégrité recherché ; de ce fait outre la publicité in extenso de la déclaration, la synthèse des contrôles fait également l’objet de publication sans préjudice des insertions au rapport général public. Elle permet d’informer l’opinion publique de la consistance du patrimoine déclaré aux fins de l’exercice de la veille citoyenne pour éventuellement faire l’écho des violations du code d’honneur par les assujettis.
                ✔	Le contradictoire (ISSAI 20) :
                Le patrimoine déclaré est évalué en espèces de la monnaie en cours par le magistrat instructeur contradictoirement lors des échanges notamment.
                ✔	Le contrôle qualité (ISSAI 140) :
                Au cours de l’instruction, le parquet est associé pour y faire ses observations par voie de conclusions. L’instruction une fois terminée, le magistrat instructeur soumet le cas au contrôle par un pair ; enfin le cas est délibéré par une formation composée de trois (3) magistrats. 
            ⮚	La méthodologie du contrôle (ISSAI 20) : C’est la phase du rapport définitif.
                ✔	La formation collégiale statue sur la valeur du patrimoine, arrête le montant et en donne acte à l’assujetti, jusqu’à plus ample informé. Les termes « jusqu’à plus ample informé » contenus dans le dispositif, impliquent la possibilité de la survenance de faits nouveaux, dénoncés par toutes voies, et de nature à faire reconsidérer l’opinion de la Cour. Ils permettent une reprise du dossier aux fins de droit à l’initiative du parquet général.
                ✔	La formation collégiale se prononce sur la licéité du patrimoine fondée sur la bonne foi de l’assujetti lorsque celle-ci n’est pas mise en doute par toutes voies notamment la veille citoyenne. 
        """;
}
    
private String prepareDelibereText(Declaration declaration, Utilisateur utilisateur) {
    StringBuilder sb = new StringBuilder();
    
    sb.append("Vu la Constitution du 25 novembre 2010 ;\n");
    sb.append("Vu la loi organique n° 2020-035 du 30 juillet 2020 déterminant les attributions, la composition, l'organisation et le fonctionnement de la Cour des comptes ;\n");
    sb.append("Vu la loi n° 2014-07 du 16 avril 2014 portant adoption du Code de transparence dans la gestion des finances publiques au sein de l'UEMOA ;\n");
    sb.append("Vu loi n° 2020-028 du 02 juillet 2020 déterminant les autres agents publics assujettis à l'obligation de déclaration des biens ;\n");
    sb.append("Vu le décret n° 2021-856/PRN/MJ du 07 octobre 2021 fixant le modèle et le contenu des déclarations des biens ;\n");
    
    String nomComplet = "N/A";
    String fonction = "N/A";
    if (declaration.getAssujetti() != null) {
        nomComplet = (declaration.getAssujetti().getPrenom() != null ? declaration.getAssujetti().getPrenom() + " " : "") +
                     (declaration.getAssujetti().getNom() != null ? declaration.getAssujetti().getNom() : "");
        fonction = declaration.getAssujetti().getFonction() != null ? 
                  declaration.getAssujetti().getFonction().getIntitule() : "N/A";
    }
    
    sb.append("Vu la déclaration initiale des biens reçue en date du ")
      .append(declaration.getDateDeclaration() != null ? 
              declaration.getDateDeclaration().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "date indéterminée")
      .append(" de Monsieur ").append(nomComplet.toUpperCase())
      .append(", ").append(fonction).append(" ;\n");
    
    sb.append("Vu l'ordonnance de désignation du conseiller rapporteur n° 0149/CDC/4ème CH en date du 29 avril 2022 ;\n");
    sb.append("Vu les conclusions du Procureur Général n° 0182/2022 en date du 12 juillet 2022 ;\n");
    sb.append("Vu le rapport provisoire n° RP- CNJ-2022-160-160-4 du 15 juillet 2022 ;\n");
    sb.append("Vu la notification du rapport provisoire au déclarant en date du 27 juillet 2022 ;\n");
    sb.append("Vu les réponses du déclarant en date du 11 août 2022 ;\n"); 
    sb.append("Ensemble les pièces du dossier ;\n\n");
    sb.append("La Cour a délibéré et adopté le rapport ci-après :");
    
    return sb.toString();
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