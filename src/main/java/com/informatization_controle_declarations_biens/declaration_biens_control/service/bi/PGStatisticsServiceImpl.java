package com.informatization_controle_declarations_biens.declaration_biens_control.service.bi;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.IRapportData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.HistoriqueDeclarationUserData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.securite.IUtilisateurData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.*;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.bi.PGStatisticsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PGStatisticsServiceImpl implements PGStatisticsService {
    private final IDeclarationData declarationData;
    private final IRapportData rapportData;
    private final IUtilisateurData utilisateurData;
    private final HistoriqueDeclarationUserData historiqueData;

    public PGStatisticsServiceImpl(IDeclarationData declarationData, 
                                 IRapportData rapportData,
                                 IUtilisateurData utilisateurData,
                                 HistoriqueDeclarationUserData historiqueData) {
        this.declarationData = declarationData;
        this.rapportData = rapportData;
        this.utilisateurData = utilisateurData;
        this.historiqueData = historiqueData;
    }

    @Override
    public DeclarationStats getDeclarationStats() {
        try {
            long total = declarationData.count();
            long initiales = declarationData.countByTypeDeclaration(TypeDeclarationEnum.Initiale);
            long misesAJour = declarationData.countByTypeDeclaration(TypeDeclarationEnum.Mise_à_jour);
            
            log.debug("Statistiques déclarations - Total: {}, Initiales: {}, Mises à jour: {}", 
                     total, initiales, misesAJour);
            
            return new DeclarationStats(total, initiales, misesAJour);
        } catch (Exception e) {
            log.error("Erreur lors du calcul des statistiques de déclarations: {}", e.getMessage());
            return new DeclarationStats(0, 0, 0);
        }
    }

    @Override
    public ReportStats getReportStats() {
        try {
            List<Rapport> rapports = rapportData.findAll();
            
            long provisoires = rapports.stream()
                    .filter(r -> r.getType() == Rapport.Type.PROVISOIRE)
                    .count();
                    
            long definitifs = rapports.stream()
                    .filter(r -> r.getType() == Rapport.Type.DEFINITIF)
                    .count();
                    
            long enRetard = historiqueData.findActiveAffectations().stream()
                    .filter(h -> h.getDateAffectation() != null && 
                               ChronoUnit.DAYS.between(h.getDateAffectation(), LocalDate.now()) > 30)
                    .count();
                    
            log.debug("Statistiques rapports - Provisoires: {}, Définitifs: {}, En retard: {}", 
                     provisoires, definitifs, enRetard);
                    
            return new ReportStats(provisoires, definitifs, enRetard);
        } catch (Exception e) {
            log.error("Erreur lors du calcul des statistiques de rapports: {}", e.getMessage());
            return new ReportStats(0, 0, 0);
        }
    }

    @Override
    public DecisionStats getDecisionStats() {
        try {
            List<Rapport> rapports = rapportData.findByType(Rapport.Type.DEFINITIF);
            
            long acceptees = rapports.stream()
                    .filter(r -> r.getDecision() != null && r.getDecision())
                    .count();
                    
            long refusees = rapports.stream()
                    .filter(r -> r.getDecision() != null && !r.getDecision())
                    .count();
                    
            log.debug("Statistiques décisions - Acceptées: {}, Refusées: {}", acceptees, refusees);
                    
            return new DecisionStats(acceptees, refusees);
        } catch (Exception e) {
            log.error("Erreur lors du calcul des statistiques de décisions: {}", e.getMessage());
            return new DecisionStats(0, 0);
        }
    }

    @Override
    public List<AdvisorPerformance> getAdvisorsPerformance() {
        try {
            List<Utilisateur> conseillers = utilisateurData.findByRole(RoleEnum.conseiller_rapporteur);
            
            return conseillers.stream()
                    .filter(conseiller -> conseiller.getId() != null && conseiller.getId() > 0)
                    .map(conseiller -> {
                        try {
                            List<Declaration> declarations = declarationData.findByUtilisateurId(conseiller.getId());
                            List<Rapport> rapports = rapportData.findByUtilisateurId(conseiller.getId());
                            
                            long acceptees = rapports.stream()
                                    .filter(r -> r.getType() == Rapport.Type.DEFINITIF && 
                                               r.getDecision() != null && r.getDecision())
                                    .count();
                                    
                            double tauxAcceptation = rapports.isEmpty() ? 0 : (acceptees * 100.0 / rapports.size());
                            
                            double tempsMoyen = declarations.stream()
                                    .filter(d -> d.getDateDeclaration() != null)
                                    .mapToLong(d -> ChronoUnit.DAYS.between(
                                            d.getDateDeclaration(), 
                                            LocalDate.now()))
                                    .average()
                                    .orElse(0);
                                    
                            return new AdvisorPerformance(
                                    conseiller.getFirstname() + " " + conseiller.getLastname(),
                                    declarations.size(),
                                    rapports.size(),
                                    tauxAcceptation,
                                    tempsMoyen
                            );
                        } catch (Exception e) {
                            log.error("Erreur lors du calcul des performances pour le conseiller {}: {}", 
                                     conseiller.getId(), e.getMessage());
                            return new AdvisorPerformance(
                                    conseiller.getFirstname() + " " + conseiller.getLastname(),
                                    0, 0, 0, 0
                            );
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erreur lors du calcul des performances des conseillers: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public TemporalStats getTemporalStats(String period) {
        try {
            List<Declaration> declarations = declarationData.findAll();
            List<Rapport> rapports = rapportData.findAll();
            
            if ("monthly".equals(period)) {
                Map<String, TemporalData> monthlyData = new LinkedHashMap<>();
                
                for (int i = 1; i <= 12; i++) {
                    Month month = Month.of(i);
                    String monthName = month.getDisplayName(TextStyle.SHORT, Locale.FRENCH);
                    
                    final int monthNum = i;
                    long decCount = declarations.stream()
                            .filter(d -> d.getDateDeclaration() != null && 
                                       d.getDateDeclaration().getMonthValue() == monthNum)
                            .count();
                            
                    long rapCount = rapports.stream()
                            .filter(r -> r.getDateCreation() != null && 
                                       r.getDateCreation().getMonthValue() == monthNum)
                            .count();
                            
                    monthlyData.put(monthName, new TemporalData(decCount, rapCount));
                }
                
                return new TemporalStats("monthly", monthlyData);
            }
            
            // Handle other periods (quarterly, yearly)
            log.warn("Période non supportée: {}", period);
            return new TemporalStats(period, new LinkedHashMap<>());
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul des statistiques temporelles pour la période {}: {}", 
                     period, e.getMessage());
            return new TemporalStats(period, new LinkedHashMap<>());
        }
    }

    @Override
    public WorkflowStats getWorkflowStats() {
        try {
            List<Declaration> declarations = declarationData.findAll();
            
            long nouveaux = declarations.stream()
                    .filter(d -> d.getEtatDeclaration() == EtatDeclarationEnum.nouveau)
                    .count();
                    
            long enCours = declarations.stream()
                    .filter(d -> d.getEtatDeclaration() == EtatDeclarationEnum.en_cours)
                    .count();
                    
            long termines = declarations.stream()
                    .filter(d -> d.getEtatDeclaration() == EtatDeclarationEnum.valider || 
                               d.getEtatDeclaration() == EtatDeclarationEnum.refuser)
                    .count();
                    
            log.debug("Statistiques workflow - Nouveaux: {}, En cours: {}, Terminés: {}", 
                     nouveaux, enCours, termines);
                    
            return new WorkflowStats(nouveaux, enCours, termines);
        } catch (Exception e) {
            log.error("Erreur lors du calcul des statistiques de workflow: {}", e.getMessage());
            return new WorkflowStats(0, 0, 0);
        }
    }
}