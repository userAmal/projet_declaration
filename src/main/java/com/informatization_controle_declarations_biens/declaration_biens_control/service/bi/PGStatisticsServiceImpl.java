package com.informatization_controle_declarations_biens.declaration_biens_control.service.bi;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.List;

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
                
        // Suppression du calcul des dossiers en retard
        long enRetard = 0; // Valeur par défaut
        
        log.debug("Statistiques rapports - Provisoires: {}, Définitifs: {}", 
                 provisoires, definitifs);
                
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