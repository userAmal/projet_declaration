package com.informatization_controle_declarations_biens.declaration_biens_control.service.bi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.ICommentaireGeneriqueData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.IRapportData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.HistoriqueDeclarationUserData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.AffectationDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.CommentaireDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.ConseillerGlobalStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.ConseillerStatisticsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.DeclarationConseillerDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.DeclarationPrioritaireDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.HistoriqueDeclarationDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.PerformanceVerificationDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.RapportDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.RapportProvisoireStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.RepartitionEtatDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.RepartitionTypeDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.StatsMensuellesDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.ValidationStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat.VerificationFraudeStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.CommentaireGenerique;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.TypeEntiteEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.HistoriqueDeclarationUser;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.UtilisateurServiceImpl;

import lombok.Builder;
import lombok.Data;
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
                .repartitionParEtat(getRepartitionDeclarationsParEtat(conseillerId))
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

// Change this:
// public List<RepartitionEtatDTO> getRepartitionDeclarationsParEtat(Long conseillerId) {
// To this:
public List<RepartitionEtatDTO> getRepartitionDeclarationsParEtat(Long conseillerId) {
    List<Declaration> declarations = historiqueData.findByUtilisateurId(conseillerId)
            .stream()
            .map(HistoriqueDeclarationUser::getDeclaration)
            .collect(Collectors.toList());
    
    Map<EtatDeclarationEnum, Long> repartition = declarations.stream()
            .collect(Collectors.groupingBy(
                    Declaration::getEtatDeclaration, 
                    Collectors.counting()
            ));
    
    return repartition.entrySet().stream()
            .map(entry -> RepartitionEtatDTO.builder()
                    .etat(entry.getKey())
                    .nombre(entry.getValue())
                    .pourcentage(declarations.isEmpty() ? 0.0 : 
                               (entry.getValue() * 100.0 / declarations.size()))
                    .build())
            .collect(Collectors.toList());
}

// Change this:
// public PerformanceVerificationDTO getPerformanceVerification(Long conseillerId) {
// To this:
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


// Méthode pour obtenir les déclarations nécessitant une attention particulière
public List<DeclarationPrioritaireDTO> getDeclarationsPrioritaires(Long conseillerId) {
    List<Declaration> declarations = declarationData.findActiveDeclarationsByConseillerId(conseillerId);
    
    return declarations.stream()
            .filter(d -> d.getDateDeclaration().isBefore(LocalDate.now().minusDays(30)))
            .map(d -> DeclarationPrioritaireDTO.builder()
                    .declarationId(d.getId())
                    .assujettiNom(d.getAssujetti().getNom())
                    .assujettiPrenom(d.getAssujetti().getPrenom())
                    .dateDeclaration(d.getDateDeclaration())
                    .joursDepuisDeclaration(ChronoUnit.DAYS.between(
                            d.getDateDeclaration(), 
                            LocalDate.now()))
                    .etatDeclaration(d.getEtatDeclaration())
                    .build())
            .sorted(Comparator.comparingLong(DeclarationPrioritaireDTO::getJoursDepuisDeclaration).reversed())
            .collect(Collectors.toList());
}

// Méthode pour obtenir la répartition par type de déclaration
public List<RepartitionTypeDTO> getRepartitionParType(Long conseillerId) {
    List<Declaration> declarations = historiqueData.findByUtilisateurId(conseillerId)
            .stream()
            .map(HistoriqueDeclarationUser::getDeclaration)
            .collect(Collectors.toList());
    
    Map<TypeDeclarationEnum, Long> repartition = declarations.stream()
            .collect(Collectors.groupingBy(
                    Declaration::getTypeDeclaration, 
                    Collectors.counting()
            ));
    
    return repartition.entrySet().stream()
            .map(entry -> RepartitionTypeDTO.builder()
                    .type(entry.getKey())
                    .nombre(entry.getValue())
                    .pourcentage(declarations.isEmpty() ? 0.0 : 
                               (entry.getValue() * 100.0 / declarations.size()))
                    .build())
            .collect(Collectors.toList());
}

// Méthode pour obtenir les statistiques de validation
public ValidationStatsDTO  getStatistiquesValidation(Long conseillerId) {
    long totalDeclarations = historiqueData.findByUtilisateurId(conseillerId).size();
    long declarationsValidees = declarationData.countByConseillerIdAndEtat(
            conseillerId, EtatDeclarationEnum.valider);
    long declarationsRejetees = declarationData.countByConseillerIdAndEtat(
            conseillerId, EtatDeclarationEnum.refuser);
    
    return ValidationStatsDTO.builder()
            .totalDeclarations(totalDeclarations)
            .declarationsValidees(declarationsValidees)
            .declarationsRejetees(declarationsRejetees)
            .tauxValidation(totalDeclarations > 0 ? 
                    (double) declarationsValidees * 100 / totalDeclarations : 0.0)
            .tauxRejet(totalDeclarations > 0 ? 
                    (double) declarationsRejetees * 100 / totalDeclarations : 0.0)
            .build();
}
// Dans ConseillerRapporteurStatisticsService
public List<ConseillerGlobalStatsDTO> getStatistiquesTousConseillers() {
    List<Utilisateur> conseillers = utilisateurServiceImpl.findByRole(RoleEnum.conseiller_rapporteur);
    
    return conseillers.stream()
            .map(c -> {
                long declarationsAssignees = getNombreDeclarationsAssignees(c.getId());
                long declarationsTraitees = getNombreDeclarationsTraitees(c.getId());
                double tempsMoyen = getTempsTraitementMoyen(c.getId());
                
                return ConseillerGlobalStatsDTO.builder()
                        .conseillerId(c.getId())
                        .conseillerNom(c.getLastname())
                        .conseillerPrenom(c.getFirstname())
                        .declarationsAssignees(declarationsAssignees)
                        .declarationsTraitees(declarationsTraitees)
                        .tempsTraitementMoyen(tempsMoyen)
                        .rapportsGeneres(getNombreRapportsProvisoires(c.getId()))
                        .observationsRealisees(getNombreObservations(c.getId()))
                        .build();
            })
            .collect(Collectors.toList());
}





}













