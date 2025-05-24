package com.informatization_controle_declarations_biens.declaration_biens_control.service.bi;


import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.AmendeData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.IRapportData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.securite.IUtilisateurData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.AmendeStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.DecisionStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.DeclarationsByActorDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.DeclarationsTrendDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.PerformanceUtilisateurDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.RaportsByTypeDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.StatsByDeclarationTypeDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.StatsByEtatDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.StatsByUserDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.StatsPeriodeDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.WorkflowStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Amende;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.StatutAmendeEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

 private final IDeclarationData declarationData;
    private final IRapportData rapportData;
    private final AmendeData amendeData;
    private final IUtilisateurData utilisateurData;

    public StatisticsService(IDeclarationData declarationData, IRapportData rapportData, 
                           AmendeData amendeData, IUtilisateurData utilisateurData) {
        this.declarationData = declarationData;
        this.rapportData = rapportData;
        this.amendeData = amendeData;
        this.utilisateurData = utilisateurData;
    }

    public long getTotalDeclarations() {
        return declarationData.count();
    }

    public RaportsByTypeDTO getReportsByType() {
        List<Rapport> allReports = rapportData.findAll();
        
        long provisoires = allReports.stream()
                .filter(r -> r.getType() == Rapport.Type.PROVISOIRE)
                .count();
        
        long definitifs = allReports.stream()
                .filter(r -> r.getType() == Rapport.Type.DEFINITIF)
                .count();
        
        return new RaportsByTypeDTO(provisoires, definitifs);
    }

    public DecisionStatsDTO getDecisionStats() {
        List<Rapport> definitiveReports = rapportData.findByType(Rapport.Type.DEFINITIF);
        
        long acceptees = definitiveReports.stream()
                .filter(r -> r.getDecision() != null && r.getDecision())
                .count();
        
        long refusees = definitiveReports.stream()
                .filter(r -> r.getDecision() != null && !r.getDecision())
                .count();
        
        return new DecisionStatsDTO(acceptees, refusees);
    }

    public DeclarationsByActorDTO getDeclarationsByActor() {
        List<Declaration> declarations = declarationData.findAll();
        
        long conseillerRapporteur = declarations.stream()
                .filter(d -> d.getUtilisateur() != null && 
                       d.getUtilisateur().getRole() == RoleEnum.conseiller_rapporteur)
                .count();
        
        long procureurGeneral = declarations.stream()
                .filter(d -> d.getUtilisateur() != null && 
                       d.getUtilisateur().getRole() == RoleEnum.procureur_general)
                .count();
        
        long avocatGeneral = declarations.stream()
                .filter(d -> d.getUtilisateur() != null && 
                       d.getUtilisateur().getRole() == RoleEnum.avocat_general)
                .count();
        
        return new DeclarationsByActorDTO(conseillerRapporteur, procureurGeneral, avocatGeneral);
    }

    public DeclarationsTrendDTO getDeclarationsTrend(String period) {
        List<Declaration> declarations = declarationData.findAll();
        LocalDate now = LocalDate.now();
        
        if ("monthly".equals(period)) {
            // Données mensuelles pour l'année en cours
            String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};
            long[] values = new long[12];
            
            declarations.stream()
                .filter(d -> d.getDateDeclaration().getYear() == now.getYear())
                .forEach(d -> {
                    int monthIndex = d.getDateDeclaration().getMonthValue() - 1;
                    values[monthIndex]++;
                });
            
            return new DeclarationsTrendDTO(months, values);
        } else if ("quarterly".equals(period)) {
            // Données trimestrielles
            String[] quarters = {"T1", "T2", "T3", "T4"};
            long[] values = new long[4];
            
            declarations.stream()
                .filter(d -> d.getDateDeclaration().getYear() == now.getYear())
                .forEach(d -> {
                    int month = d.getDateDeclaration().getMonthValue();
                    int quarterIndex = (month - 1) / 3;
                    values[quarterIndex]++;
                });
            
            return new DeclarationsTrendDTO(quarters, values);
        }
        
        return new DeclarationsTrendDTO(new String[0], new long[0]);
    }

    public List<StatsByUserDTO> getStatsByUser() {
        List<Declaration> declarations = declarationData.findAll();
        List<Rapport> reports = rapportData.findAll();
        
        // Grouper les déclarations par utilisateur
        Map<Utilisateur, List<Declaration>> declarationsByUser = declarations.stream()
                .filter(d -> d.getUtilisateur() != null)
                .collect(Collectors.groupingBy(Declaration::getUtilisateur));
        
        List<StatsByUserDTO> stats = new ArrayList<>();
        
        declarationsByUser.forEach((user, userDeclarations) -> {
            long acceptees = 0;
            long refusees = 0;
            
            // Pour chaque déclaration, chercher le rapport définitif correspondant
            for (Declaration declaration : userDeclarations) {
                Optional<Rapport> definitiveReport = reports.stream()
                        .filter(r -> r.getDeclaration().getId().equals(declaration.getId()) && 
                               r.getType() == Rapport.Type.DEFINITIF)
                        .findFirst();
                
                if (definitiveReport.isPresent() && definitiveReport.get().getDecision() != null) {
                    if (definitiveReport.get().getDecision()) {
                        acceptees++;
                    } else {
                        refusees++;
                    }
                }
            }
            
            stats.add(new StatsByUserDTO(
                user.getFirstname() + " " + user.getLastname(),
                userDeclarations.size(),
                acceptees,
                refusees
            ));
        });
        
        return stats;
    }

    public List<StatsByDeclarationTypeDTO> getStatsByDeclarationType() {
        List<Declaration> declarations = declarationData.findAll();
        
        // Grouper par type de déclaration
        Map<TypeDeclarationEnum, Long> countByType = declarations.stream()
                .collect(Collectors.groupingBy(Declaration::getTypeDeclaration, Collectors.counting()));
        
        List<StatsByDeclarationTypeDTO> stats = new ArrayList<>();
        
        countByType.forEach((type, count) -> {
            stats.add(new StatsByDeclarationTypeDTO(type.name(), count));
        });
        
        return stats;
    }
     public AmendeStatsDTO getAmendeStats() {
        List<Amende> amendes = amendeData.findAll();
        
        long totalAmendes = amendes.size();
        long amendesPayees = amendes.stream()
                .map(a -> a.getStatut() == StatutAmendeEnum.Payee ? 1L : 0L)
                .reduce(0L, Long::sum);
        long amendesNonPayees = amendes.stream()
                .map(a -> a.getStatut() == StatutAmendeEnum.NonPayee ? 1L : 0L)
                .reduce(0L, Long::sum);
        long amendesAnnulees = amendes.stream()
                .map(a -> a.getStatut() == StatutAmendeEnum.Annulee ? 1L : 0L)
                .reduce(0L, Long::sum);
        
        BigDecimal montantTotal = amendes.stream()
                .map(Amende::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal montantPaye = amendes.stream()
                .filter(a -> a.getStatut() == StatutAmendeEnum.Payee)
                .map(Amende::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal montantEnAttente = amendes.stream()
                .filter(a -> a.getStatut() == StatutAmendeEnum.NonPayee)
                .map(Amende::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new AmendeStatsDTO(totalAmendes, amendesPayees, amendesNonPayees, 
                                 amendesAnnulees, montantTotal, montantPaye, montantEnAttente);
    }

    public List<StatsByEtatDTO> getStatsByEtat() {
        List<Declaration> declarations = declarationData.findAll();
        long total = declarations.size();
        
        Map<EtatDeclarationEnum, Long> countByEtat = declarations.stream()
                .collect(Collectors.groupingBy(Declaration::getEtatDeclaration, Collectors.counting()));
        
        return countByEtat.entrySet().stream()
                .map(entry -> new StatsByEtatDTO(
                    entry.getKey().name(),
                    entry.getValue(),
                    total > 0 ? (entry.getValue() * 100.0 / total) : 0.0
                ))
                .collect(Collectors.toList());
    }

    public List<StatsPeriodeDto> getStatsByPeriode(String periode) {
        List<Declaration> declarations = declarationData.findAll();
        List<Rapport> rapports = rapportData.findAll();
        List<Amende> amendes = amendeData.findAll();
        
        List<StatsPeriodeDto> stats = new ArrayList<>();
        LocalDate now = LocalDate.now();
        
        if ("monthly".equals(periode)) {
            for (int i = 1; i <= 12; i++) {
                final int month = i;
                String moisNom = Month.of(i).getDisplayName(TextStyle.SHORT, Locale.FRENCH);
                
                long declarationsCount = declarations.stream()
                        .filter(d -> d.getDateDeclaration().getMonthValue() == month && 
                                   d.getDateDeclaration().getYear() == now.getYear())
                        .count();
                
                long rapportsProvisoires = rapports.stream()
                        .filter(r -> r.getType() == Rapport.Type.PROVISOIRE &&
                                   r.getDateCreation().getMonthValue() == month &&
                                   r.getDateCreation().getYear() == now.getYear())
                        .count();
                
                long rapportsDefinitifs = rapports.stream()
                        .filter(r -> r.getType() == Rapport.Type.DEFINITIF &&
                                   r.getDateCreation().getMonthValue() == month &&
                                   r.getDateCreation().getYear() == now.getYear())
                        .count();
                
                long acceptees = rapports.stream()
                        .filter(r -> r.getType() == Rapport.Type.DEFINITIF &&
                                   r.getDecision() != null && r.getDecision() &&
                                   r.getDateCreation().getMonthValue() == month &&
                                   r.getDateCreation().getYear() == now.getYear())
                        .count();
                
                long refusees = rapports.stream()
                        .filter(r -> r.getType() == Rapport.Type.DEFINITIF &&
                                   r.getDecision() != null && !r.getDecision() &&
                                   r.getDateCreation().getMonthValue() == month &&
                                   r.getDateCreation().getYear() == now.getYear())
                        .count();
                
                long amendesCount = amendes.stream()
                        .filter(a -> a.getDateAmende().getMonthValue() == month &&
                                   a.getDateAmende().getYear() == now.getYear())
                        .count();
                
                stats.add(new StatsPeriodeDto(moisNom, declarationsCount, rapportsProvisoires,
                                            rapportsDefinitifs, acceptees, refusees, amendesCount));
            }
        }
        
        return stats;
    }

    public List<PerformanceUtilisateurDTO> getPerformanceUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurData.findAll();
        List<Declaration> declarations = declarationData.findAll();
        List<Rapport> rapports = rapportData.findAll();
        
        return utilisateurs.stream()
                .filter(u -> u.getRole() == RoleEnum.conseiller_rapporteur || 
                           u.getRole() == RoleEnum.avocat_general ||
                           u.getRole() == RoleEnum.procureur_general)
                .map(utilisateur -> {
                    List<Declaration> userDeclarations = declarations.stream()
                            .filter(d -> d.getUtilisateur() != null && 
                                       d.getUtilisateur().getId().equals(utilisateur.getId()))
                            .collect(Collectors.toList());
                    
                    List<Rapport> userRapports = rapports.stream()
                            .filter(r -> r.getUtilisateur().getId().equals(utilisateur.getId()))
                            .collect(Collectors.toList());
                    
                    long acceptees = userRapports.stream()
                            .filter(r -> r.getType() == Rapport.Type.DEFINITIF &&
                                       r.getDecision() != null && r.getDecision())
                            .count();
                    
                    double tauxAcceptation = userRapports.size() > 0 ? 
                            (acceptees * 100.0 / userRapports.size()) : 0.0;
                    
                    // Calcul approximatif du temps de traitement moyen
                    double tempsTraitementMoyen = calculateAverageProcessingTime(userDeclarations);
                    
                    return new PerformanceUtilisateurDTO(
                            utilisateur.getFirstname() + " " + utilisateur.getLastname(),
                            utilisateur.getRole().name(),
                            userDeclarations.size(),
                            userRapports.size(),
                            tempsTraitementMoyen,
                            tauxAcceptation
                    );
                })
                .collect(Collectors.toList());
    }

    public WorkflowStatsDTO getWorkflowStats() {
        List<Declaration> declarations = declarationData.findAll();
        
        long enAttente = declarations.stream()
                .filter(d -> d.getEtatDeclaration() == EtatDeclarationEnum.nouveau)
                .count();
        
        long enTraitement = declarations.stream()
                .filter(d -> d.getEtatDeclaration() == EtatDeclarationEnum.en_cours)
                .count();
        
        long terminees = declarations.stream()
                .filter(d -> d.getEtatDeclaration() == EtatDeclarationEnum.valider ||
                           d.getEtatDeclaration() == EtatDeclarationEnum.refuser)
                .count();
        
        double tempsTraitementMoyen = calculateAverageProcessingTime(
                declarations.stream()
                        .filter(d -> d.getEtatDeclaration() == EtatDeclarationEnum.valider ||
                                   d.getEtatDeclaration() == EtatDeclarationEnum.refuser)
                        .collect(Collectors.toList())
        );
        
        // Calcul approximatif des retards (déclarations en traitement depuis plus de 30 jours)
        long retards = declarations.stream()
                .filter(d -> d.getEtatDeclaration() == EtatDeclarationEnum.en_cours &&
                           ChronoUnit.DAYS.between(d.getDateDeclaration(), LocalDate.now()) > 30)
                .count();
        
        return new WorkflowStatsDTO(enAttente, enTraitement, terminees, tempsTraitementMoyen, retards);
    }

    private double calculateAverageProcessingTime(List<Declaration> declarations) {
        if (declarations.isEmpty()) return 0.0;
        
        return declarations.stream()
                .mapToLong(d -> ChronoUnit.DAYS.between(d.getDateDeclaration(), LocalDate.now()))
                .average()
                .orElse(0.0);
    }

    // ==================== MÉTHODES SPÉCIFIQUES PAR RÔLE ====================

    public Map<String, Object> getStatsForAdmin() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDeclarations", getTotalDeclarations());
        stats.put("rapportsByType", getReportsByType());
        stats.put("decisionStats", getDecisionStats());
        stats.put("declarationsByActor", getDeclarationsByActor());
        stats.put("declarationsTrend", getDeclarationsTrend("monthly"));
        stats.put("statsByUser", getStatsByUser());
        stats.put("statsByDeclarationType", getStatsByDeclarationType());
        stats.put("amendeStats", getAmendeStats());
        stats.put("statsByEtat", getStatsByEtat());
        stats.put("statsByPeriode", getStatsByPeriode("monthly"));
        stats.put("performanceUtilisateurs", getPerformanceUtilisateurs());
        stats.put("workflowStats", getWorkflowStats());
        return stats;
    }

    public Map<String, Object> getStatsForProcureurGeneral() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDeclarations", getTotalDeclarations());
        stats.put("rapportsByType", getReportsByType());
        stats.put("decisionStats", getDecisionStats());
        stats.put("declarationsByActor", getDeclarationsByActor());
        stats.put("declarationsTrend", getDeclarationsTrend("monthly"));
        stats.put("statsByDeclarationType", getStatsByDeclarationType());
        stats.put("statsByEtat", getStatsByEtat());
        stats.put("performanceUtilisateurs", getPerformanceUtilisateurs());
        stats.put("workflowStats", getWorkflowStats());
        return stats;
    }

    public Map<String, Object> getStatsForConseillerRapporteur(Long utilisateurId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Stats personnelles
        List<Declaration> userDeclarations = declarationData.findByUtilisateurId(utilisateurId);
        List<Rapport> userRapports = rapportData.findByUtilisateurId(utilisateurId);
        
        stats.put("mesDeclarations", userDeclarations.size());
        stats.put("mesRapports", userRapports.size());
        stats.put("rapportsProvisoires", userRapports.stream()
                .filter(r -> r.getType() == Rapport.Type.PROVISOIRE).count());
        stats.put("rapportsDefinitifs", userRapports.stream()
                .filter(r -> r.getType() == Rapport.Type.DEFINITIF).count());
        
        // Stats globales limitées
        stats.put("totalDeclarations", getTotalDeclarations());
        stats.put("declarationsTrend", getDeclarationsTrend("monthly"));
        
        return stats;
    }

    public Map<String, Object> getStatsForAvocatGeneral(Long utilisateurId) {
        return getStatsForConseillerRapporteur(utilisateurId); // Même logique
    }
}
