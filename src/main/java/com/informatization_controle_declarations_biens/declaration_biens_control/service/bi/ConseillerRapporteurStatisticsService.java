package com.informatization_controle_declarations_biens.declaration_biens_control.service.bi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.ICommentaireGeneriqueData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.IRapportData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.HistoriqueDeclarationUserData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.ChargeUtilisateurDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.ConseillerStatisticsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.DeclarationAncienneDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.DeclarationConseillerDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.PerformanceAnnuelleDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.PerformanceVerificationDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.RapportProvisoireStatsDTO;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.StatsMensuellesDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.VerificationFraudeStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.CommentaireGenerique;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.HistoriqueDeclarationUser;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.UtilisateurServiceImpl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConseillerRapporteurStatisticsService {

    private final IDeclarationData declarationData;
    private final IRapportData rapportData;
    private final ICommentaireGeneriqueData commentaireData;
    private final HistoriqueDeclarationUserData historiqueData;
    private final UtilisateurServiceImpl utilisateurServiceImpl;

    public ConseillerStatisticsDTO getStatistiquesConseiller(Long conseillerId) {
        log.info("Génération des statistiques pour le conseiller ID: {}", conseillerId);
        
        return ConseillerStatisticsDTO.builder()
                .declarationsAssignees(getNombreDeclarationsAssignees(conseillerId))
                .rapportsProvisoiresGeneres(getNombreRapportsProvisoires(conseillerId))
                .observationsRealisees(getNombreObservations(conseillerId))
                .declarationsTraitees(getNombreDeclarationsTraitees(conseillerId))
                .declarationsEnCours(getNombreDeclarationsEnCours(conseillerId))
                .tempsTraitementMoyen(getTempsTraitementMoyen(conseillerId))
                .statistiquesParMois(getStatistiquesParMois(conseillerId))
                .performanceVerification(getPerformanceVerification(conseillerId))
                .build();
    }

    public List<DeclarationConseillerDTO> consulterDeclarationsAssignees(Long conseillerId) {
        log.info("Consultation des déclarations assignées au conseiller ID: {}", conseillerId);
        
        List<HistoriqueDeclarationUser> affectationsActives = historiqueData
                .findByUtilisateurId(conseillerId)
                .stream()
                .filter(h -> h.getDateFinAffectation() == null)
                .collect(Collectors.toList());

        return affectationsActives.stream()
                .map(historique -> {
                    Declaration declaration = historique.getDeclaration();
                    boolean rapportProvisoireGenere = rapportData
                            .findByDeclarationId(declaration.getId())
                            .stream()
                            .anyMatch(r -> r.getType() == Rapport.Type.PROVISOIRE);
                    
                    long nombreObservations = commentaireData
                            .findByUtilisateurIdAndDeclarationId(
                                    conseillerId, 
                                    declaration.getId()
                            ).size();

                    return DeclarationConseillerDTO.builder()
                            .declarationId(declaration.getId())
                            .assujettiNom(declaration.getAssujetti().getNom())
                            .assujettiPrenom(declaration.getAssujetti().getPrenom())
                            .dateDeclaration(declaration.getDateDeclaration())
                            .typeDeclaration(declaration.getTypeDeclaration())
                            .etatDeclaration(declaration.getEtatDeclaration())
                            .dateAffectation(historique.getDateAffectation())
                            .rapportProvisoireGenere(rapportProvisoireGenere)
                            .nombreObservations(nombreObservations)
                            .joursTraitement(ChronoUnit.DAYS.between(
                                    historique.getDateAffectation(), 
                                    LocalDate.now()
                            ))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public RapportProvisoireStatsDTO genererRapportProvisoire(Long conseillerId, Long declarationId) {
        log.info("Génération du rapport provisoire - Conseiller: {}, Déclaration: {}", 
                conseillerId, declarationId);
        
        Declaration declaration = declarationData.findById(declarationId)
                .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));
        
        boolean estAffecte = historiqueData.existsByDeclarationIdAndUtilisateurId(declarationId, conseillerId);
        if (!estAffecte) {
            throw new RuntimeException("Conseiller non affecté à cette déclaration");
        }
        
        List<CommentaireGenerique> observations = commentaireData
                .findByUtilisateurIdAndDeclarationId(
                        conseillerId, 
                        declarationId
                );
        
        boolean rapportExistant = rapportData.findByDeclarationIdAndType(
                declarationId, 
                Rapport.Type.PROVISOIRE
        ).stream().anyMatch(r -> r.getUtilisateur().getId().equals(conseillerId));
        
        return RapportProvisoireStatsDTO.builder()
                .declarationId(declarationId)
                .conseillerId(conseillerId)
                .nombreObservations(observations.size())
                .rapportDejaGenere(rapportExistant)
                .dateGeneration(rapportExistant ? LocalDate.now() : null)
                .assujettiInfo(declaration.getAssujetti().getNom() + " " + 
                              declaration.getAssujetti().getPrenom())
                .typeDeclaration(declaration.getTypeDeclaration())
                .build();
    }

    public VerificationFraudeStatsDTO verifierDeclaration(Long conseillerId, Long declarationId) {
        log.info("Vérification de fraude - Conseiller: {}, Déclaration: {}", 
                conseillerId, declarationId);
        
        Declaration declaration = declarationData.findById(declarationId)
                .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));
        
        List<String> anomaliesDetectees = new ArrayList<>();
        int scoreRisque = 0;
        
        if (declaration.getTypeDeclaration() == TypeDeclarationEnum.Mise_à_jour) {
            anomaliesDetectees.add("Vérification de cohérence avec déclarations antérieures requise");
            scoreRisque += 10;
        }
        
        return VerificationFraudeStatsDTO.builder()
                .declarationId(declarationId)
                .conseillerId(conseillerId)
                .scoreRisque(scoreRisque)
                .anomaliesDetectees(anomaliesDetectees)
                .dateVerification(LocalDate.now())
                .necessiteVerificationApprofondie(scoreRisque > 50)
                .assujettiInfo(declaration.getAssujetti().getNom() + " " + 
                              declaration.getAssujetti().getPrenom())
                .build();
    }

    public long getNombreDeclarationsAssignees(Long conseillerId) {
        return historiqueData.findByUtilisateurId(conseillerId)
                .stream()
                .filter(h -> h.getDateFinAffectation() == null)
                .count();
    }

    public long getNombreRapportsProvisoires(Long conseillerId) {
        return rapportData.findByUtilisateurId(conseillerId)
                .stream()
                .filter(r -> r.getType() == Rapport.Type.PROVISOIRE)
                .count();
    }

    public long getNombreObservations(Long conseillerId) {
        return commentaireData.findByUtilisateurId(conseillerId)
                .stream()
                .count();
    }

    public long getNombreDeclarationsTraitees(Long conseillerId) {
        return historiqueData.findByUtilisateurId(conseillerId)
                .stream()
                .filter(h -> h.getDateFinAffectation() != null)
                .count();
    }

    public long getNombreDeclarationsEnCours(Long conseillerId) {
        return historiqueData.findByUtilisateurId(conseillerId)
                .stream()
                .filter(h -> h.getDateFinAffectation() == null)
                .filter(h -> h.getDeclaration().getEtatDeclaration() == EtatDeclarationEnum.en_cours)
                .count();
    }

    public double getTempsTraitementMoyen(Long conseillerId) {
        return historiqueData.findByUtilisateurId(conseillerId)
                .stream()
                .filter(h -> h.getDateFinAffectation() != null)
                .mapToLong(h -> ChronoUnit.DAYS.between(
                        h.getDateAffectation(), 
                        h.getDateFinAffectation()
                ))
                .average()
                .orElse(0.0);
    }

 
    public double calculateEfficiencyScore(Long conseillerId) {
        double tempsTraitement = getTempsTraitementMoyen(conseillerId);
        long declarationsTraitees = getNombreDeclarationsTraitees(conseillerId);
        
        double scoreTemps = tempsTraitement > 0 ? Math.max(0, 100 - tempsTraitement * 2) : 0;
        double scoreVolume = Math.min(100, declarationsTraitees * 5);
        
        return (scoreTemps * 0.4 + scoreVolume * 0.6); // Pondération 40% temps, 60% volume
    }
    // In your ConseillerRapporteurStatisticsService class, change these methods from public to public:

// Change this:
// public List<StatsMensuellesDTO> getStatistiquesParMois(Long conseillerId) {
// To this:
public List<StatsMensuellesDTO> getStatistiquesParMois(Long conseillerId) {
    List<HistoriqueDeclarationUser> historiques = historiqueData.findByUtilisateurId(conseillerId);
    LocalDate maintenant = LocalDate.now();
    
    return IntStream.range(0, 12)
            .mapToObj(i -> {
                LocalDate mois = maintenant.minusMonths(i);
                String nomMois = mois.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
                
                long affectations = historiques.stream()
                        .filter(h -> h.getDateAffectation().getMonth() == mois.getMonth() &&
                                   h.getDateAffectation().getYear() == mois.getYear())
                        .count();
                
                long traitements = historiques.stream()
                        .filter(h -> h.getDateFinAffectation() != null &&
                                   h.getDateFinAffectation().getMonth() == mois.getMonth() &&
                                   h.getDateFinAffectation().getYear() == mois.getYear())
                        .count();
                
                return StatsMensuellesDTO.builder()
                        .mois(nomMois)
                        .annee(mois.getYear())
                        .declarationsRecues(affectations)
                        .declarationsTraitees(traitements)
                        .build();
            })
            .sorted((a, b) -> {
                if (a.getAnnee() == b.getAnnee()) {
                    return LocalDate.parse("01 " + a.getMois() + " " + a.getAnnee(), 
                            DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH))
                            .compareTo(
                                    LocalDate.parse("01 " + b.getMois() + " " + b.getAnnee(), 
                                    DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH)));
                }
                return Integer.compare(a.getAnnee(), b.getAnnee());
            })
            .collect(Collectors.toList());
}

public PerformanceVerificationDTO getPerformanceVerification(Long conseillerId) {
    long rapportsProvisoires = rapportData.findByUtilisateurId(conseillerId)
            .stream()
            .filter(r -> r.getType() == Rapport.Type.PROVISOIRE)
            .count();
    
    long totalObservations = commentaireData.findByUtilisateurId(conseillerId)
            .stream()
            .count();
    
    return PerformanceVerificationDTO.builder()
            .totalRapportsGeneres(rapportsProvisoires)
            .totalObservations(totalObservations)
            .moyenneObservationsParRapport(rapportsProvisoires > 0 ? 
                    (double) totalObservations / rapportsProvisoires : 0.0)
            .efficaciteTraitement(calculateEfficiencyScore(conseillerId))
            .build();
}
// Ajouter ces méthodes dans votre ConseillerRapporteurStatisticsService existant

/**
 * Vérifie si une LocalDateTime est dans la période donnée (mois courant)
 */
private boolean estDansLeMoisCourant(LocalDateTime dateTime) {
    if (dateTime == null) return false;
    LocalDate date = dateTime.toLocalDate();
    LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
    LocalDate finMois = debutMois.plusMonths(1).minusDays(1);
    return !date.isBefore(debutMois) && !date.isAfter(finMois);
}

/**
 * Vérifie si une LocalDateTime est dans l'année courante
 */
private boolean estDansLAnneeCourante(LocalDateTime dateTime) {
    if (dateTime == null) return false;
    LocalDate date = dateTime.toLocalDate();
    int anneeActuelle = LocalDate.now().getYear();
    return date.getYear() == anneeActuelle;
}


/**
 * Obtenir la charge de travail d'un utilisateur spécifique
 */
public ChargeUtilisateurDTO getChargeUtilisateur(Long utilisateurId) {
    log.info("Calcul de la charge de travail pour l'utilisateur ID: {}", utilisateurId);
    
    // Déclarations en cours
    long declarationsEnCours = historiqueData.findByUtilisateurId(utilisateurId)
            .stream()
            .filter(h -> h.getDateFinAffectation() == null)
            .count();
    
    // Déclarations traitées ce mois
    LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
    long declarationsTraiteesMois = historiqueData.findByUtilisateurId(utilisateurId)
            .stream()
            .filter(h -> h.getDateFinAffectation() != null)
            .filter(h -> h.getDateFinAffectation().isAfter(debutMois))
            .count();
    
    // Temps moyen de traitement
    double tempsMoyenTraitement = getTempsTraitementMoyen(utilisateurId);
    
    // Rapports provisoires générés ce mois
    long rapportsProvMois = rapportData.findByUtilisateurId(utilisateurId)
            .stream()
            .filter(r -> r.getType() == Rapport.Type.PROVISOIRE)
            .filter(r -> estDansLeMoisCourant(r.getDateCreation()))
            .count();
    
    
    // Score de charge (plus élevé = plus chargé)
    double scoreCharge = calculerScoreCharge(declarationsEnCours, tempsMoyenTraitement, 
                                            declarationsTraiteesMois);
    
    // Récupérer les infos utilisateur
    Optional<Utilisateur> utilisateurOpt = utilisateurServiceImpl.findById(utilisateurId);
    if (utilisateurOpt.isEmpty()) {
        throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + utilisateurId);
    }
    Utilisateur utilisateur = utilisateurOpt.get();
    
    return ChargeUtilisateurDTO.builder()
            .utilisateurId(utilisateurId)
            .nom(utilisateur.getLastname())
            .prenom(utilisateur.getFirstname())
            .role(utilisateur.getRole())
            .declarationsEnCours(declarationsEnCours)
            .declarationsTraiteesMois(declarationsTraiteesMois)
            .tempsMoyenTraitement(tempsMoyenTraitement)
            .rapportsProvMois(rapportsProvMois)
            .scoreCharge(scoreCharge)
            .statut(determinerStatutCharge(scoreCharge))
            .build();
}

/**
 * Performance d'un utilisateur pour l'année courante
 */
public PerformanceAnnuelleDTO getPerformanceAnnuelleCourante(Long utilisateurId) {
    log.info("Calcul de la performance annuelle pour l'utilisateur ID: {}", utilisateurId);
    
    int anneeActuelle = LocalDate.now().getYear();
    LocalDate debutAnnee = LocalDate.of(anneeActuelle, 1, 1);
    LocalDate finAnnee = LocalDate.of(anneeActuelle, 12, 31);
    
    // Déclarations traitées cette année
    long declarationsTraiteesAnnee = historiqueData.findByUtilisateurId(utilisateurId)
            .stream()
            .filter(h -> h.getDateFinAffectation() != null)
            .filter(h -> h.getDateFinAffectation().isAfter(debutAnnee.minusDays(1)) && 
                        h.getDateFinAffectation().isBefore(finAnnee.plusDays(1)))
            .count();
    
    // Rapports générés cette année
    long rapportsGeneres = rapportData.findByUtilisateurId(utilisateurId)
            .stream()
            .filter(r -> estDansLAnneeCourante(r.getDateCreation()))
            .count();
    
  
    // Score d'efficacité pour l'année
    double scoreEfficacite = calculateEfficiencyScore(utilisateurId);
    
    // Moyennes mensuelles
    double moyenneDeclarationsParMois = declarationsTraiteesAnnee / 12.0;
    double moyenneRapportsParMois = rapportsGeneres / 12.0;
    
    // Récupérer les infos utilisateur
    Optional<Utilisateur> utilisateurOpt = utilisateurServiceImpl.findById(utilisateurId);
    if (utilisateurOpt.isEmpty()) {
        throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + utilisateurId);
    }
    Utilisateur utilisateur = utilisateurOpt.get();
    
    return PerformanceAnnuelleDTO.builder()
            .utilisateurId(utilisateurId)
            .nom(utilisateur.getLastname())
            .prenom(utilisateur.getFirstname())
            .role(utilisateur.getRole())
            .annee(anneeActuelle)
            .declarationsTraiteesAnnee(declarationsTraiteesAnnee)
            .rapportsGeneresAnnee(rapportsGeneres)
            .tempsMoyenTraitement(getTempsTraitementMoyen(utilisateurId))
            .moyenneDeclarationsParMois(moyenneDeclarationsParMois)
            .moyenneRapportsParMois(moyenneRapportsParMois)
            .scoreEfficacite(scoreEfficacite)
            .niveauPerformance(determinerNiveauPerformance(scoreEfficacite))
            .build();
}

/**
 * Déclarations les plus anciennes nécessitant un contrôle pour un utilisateur
 */
public List<DeclarationAncienneDTO> getDeclarationsAnciennesAControler(Long utilisateurId) {
    log.info("Recherche des déclarations anciennes à contrôler pour l'utilisateur ID: {}", utilisateurId);
    
    LocalDate limiteDate = LocalDate.now().minusDays(30); // Plus de 30 jours
    
    List<HistoriqueDeclarationUser> affectationsActives = historiqueData
            .findByUtilisateurId(utilisateurId)
            .stream()
            .filter(h -> h.getDateFinAffectation() == null) // Encore en cours
            .filter(h -> h.getDateAffectation().isBefore(limiteDate)) // Ancienne
            .collect(Collectors.toList());

    return affectationsActives.stream()
            .map(historique -> {
                Declaration declaration = historique.getDeclaration();
                
                // Calculer l'ancienneté en jours
                long joursAnciennete = ChronoUnit.DAYS.between(
                        historique.getDateAffectation(), 
                        LocalDate.now()
                );
                
                // Vérifier si un rapport provisoire existe
                boolean rapportProvisoireExiste = rapportData
                        .findByDeclarationId(declaration.getId())
                        .stream()
                        .anyMatch(r -> r.getType() == Rapport.Type.PROVISOIRE);
                
                // Nombre d'observations
                long nombreObservations = commentaireData
                        .findByUtilisateurIdAndDeclarationId(
                                utilisateurId, 
                                declaration.getId()
                        ).size();
                
                // Niveau de priorité basé sur l'ancienneté
                String niveauPriorite = determinerNiveauPriorite(joursAnciennete);
                
                return DeclarationAncienneDTO.builder()
                        .declarationId(declaration.getId())
                        .assujettiNom(declaration.getAssujetti().getNom())
                        .assujettiPrenom(declaration.getAssujetti().getPrenom())
                        .typeDeclaration(declaration.getTypeDeclaration())
                        .etatDeclaration(declaration.getEtatDeclaration())
                        .dateDeclaration(declaration.getDateDeclaration())
                        .dateAffectation(historique.getDateAffectation())
                        .joursAnciennete(joursAnciennete)
                        .rapportProvisoireExiste(rapportProvisoireExiste)
                        .nombreObservations(nombreObservations)
                        .niveauPriorite(niveauPriorite)
                        .recommandationAction(genererRecommandation(joursAnciennete, rapportProvisoireExiste, nombreObservations))
                        .build();
            })
            .sorted((a, b) -> Long.compare(b.getJoursAnciennete(), a.getJoursAnciennete())) // Plus ancien en premier
            .collect(Collectors.toList());
}

/**
 * Calculer le score de charge d'un utilisateur
 */
private double calculerScoreCharge(long declarationsEnCours, double tempsMoyenTraitement, 
                                  long declarationsTraiteesMois) {
    // Pondération : 40% déclarations en cours, 30% temps moyen, 30% productivité
    double scoreEnCours = Math.min(100, declarationsEnCours * 10); // Max 100
    double scoreTemps = tempsMoyenTraitement > 0 ? Math.min(100, tempsMoyenTraitement * 3) : 0;
    double scoreProductivite = Math.max(0, 100 - (declarationsTraiteesMois * 5)); // Moins de traités = plus chargé
    
    return (scoreEnCours * 0.4) + (scoreTemps * 0.3) + (scoreProductivite * 0.3);
}

/**
 * Déterminer le statut de charge
 */
private String determinerStatutCharge(double scoreCharge) {
    if (scoreCharge >= 70) return "SURCHARGE";
    if (scoreCharge >= 50) return "CHARGE_ELEVEE";
    if (scoreCharge >= 30) return "CHARGE_NORMALE";
    return "SOUS_CHARGE";
}

/**
 * Déterminer le niveau de performance
 */
private String determinerNiveauPerformance(double scoreEfficacite) {
    if (scoreEfficacite >= 80) return "EXCELLENT";
    if (scoreEfficacite >= 60) return "BON";
    if (scoreEfficacite >= 40) return "MOYEN";
    return "FAIBLE";
}

/**
 * Déterminer le niveau de priorité basé sur l'ancienneté
 */
private String determinerNiveauPriorite(long joursAnciennete) {
    if (joursAnciennete >= 90) return "CRITIQUE";
    if (joursAnciennete >= 60) return "URGENT";
    if (joursAnciennete >= 30) return "IMPORTANT";
    return "NORMAL";
}

/**
 * Générer une recommandation d'action
 */
private String genererRecommandation(long joursAnciennete, boolean rapportProvisoireExiste, 
                                   long nombreObservations) {
    if (joursAnciennete >= 90) {
        return "Action immédiate requise - Escalader au superviseur";
    }
    if (joursAnciennete >= 60 && !rapportProvisoireExiste) {
        return "Générer le rapport provisoire en urgence";
    }
    if (joursAnciennete >= 45 && nombreObservations == 0) {
        return "Commencer l'analyse et ajouter des observations";
    }
    if (joursAnciennete >= 30) {
        return "Accélérer le traitement de cette déclaration";
    }
    return "Continuer le traitement normal";
}
}
