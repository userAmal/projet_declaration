package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.securite.IUtilisateurData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.IRapportData;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class AdvancedStatisticsService {
    
    private final IDeclarationData declarationData;
    private final IUtilisateurData utilisateurData;
    private final IRapportData rapportData;
    
    public AdvancedStatisticsService(IDeclarationData declarationData,
                                   IUtilisateurData utilisateurData,
                                   IRapportData rapportData) {
        this.declarationData = declarationData;
        this.utilisateurData = utilisateurData;
        this.rapportData = rapportData;
    }
    
    
    /**
     * Statistiques de charge de travail pour un utilisateur spécifique
     */
    public UserWorkloadStatsDto getUserWorkloadStats(Long userId) {
        Utilisateur user = utilisateurData.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé: " + userId));
        
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfMonth = currentDate.withDayOfMonth(1);
        LocalDate startOfYear = currentDate.withDayOfYear(1);
        
        return calculateUserWorkload(user, startOfMonth, startOfYear, currentDate);
    }
    
    private UserWorkloadStatsDto calculateUserWorkload(Utilisateur user, LocalDate startOfMonth, 
                                                      LocalDate startOfYear, LocalDate currentDate) {
        
        // États en cours de traitement
        List<EtatDeclarationEnum> etatsEnCours = Arrays.asList(
                EtatDeclarationEnum.en_cours, EtatDeclarationEnum.traitement
        );
        
        List<EtatDeclarationEnum> etatsTermines = Arrays.asList(
                EtatDeclarationEnum.valider, EtatDeclarationEnum.refuser
        );
        
        // Charge actuelle
        Long declarationsEnCours = declarationData.countByUtilisateurIdAndEtatDeclarationIn(
                user.getId(), etatsEnCours);
        
        Long declarationsNouvelles = declarationData.countByUtilisateurIdAndEtatDeclaration(
                user.getId(), EtatDeclarationEnum.nouveau);
        
        Long declarationsEnAttente = declarationData.countByUtilisateurIdAndEtatDeclaration(
                user.getId(), EtatDeclarationEnum.No_declaré);
        
        // Performance mensuelle
        Long declarationsTraiteesThisMois = declarationData.countByUtilisateurIdAndEtatDeclarationInAndDateBetween(
                user.getId(), etatsTermines, startOfMonth, currentDate);
        
        // Performance annuelle  
        Long declarationsTraiteesThisYear = declarationData.countByUtilisateurIdAndEtatDeclarationInAndDateBetween(
                user.getId(), etatsTermines, startOfYear, currentDate);
        
        // Calcul des taux de réussite
        Long totalMonthDeclarations = declarationData.countByUtilisateurIdAndEtatDeclarationInAndDateBetween(
                user.getId(), Arrays.asList(EtatDeclarationEnum.values()), startOfMonth, currentDate);
        
        Double tauxReussiteThisMois = totalMonthDeclarations > 0 ? 
                (declarationsTraiteesThisMois.doubleValue() / totalMonthDeclarations) * 100 : 0.0;
        
        Long totalYearDeclarations = declarationData.countByUtilisateurIdAndEtatDeclarationInAndDateBetween(
                user.getId(), Arrays.asList(EtatDeclarationEnum.values()), startOfYear, currentDate);
        
        Double tauxReussiteThisYear = totalYearDeclarations > 0 ? 
                (declarationsTraiteesThisYear.doubleValue() / totalYearDeclarations) * 100 : 0.0;
        
        // Score de charge (pondéré)
        Long scoreCharge = (declarationsEnCours * 3L) + 
                          (declarationsNouvelles * 2L) + 
                          (declarationsEnAttente * 4L);
        
        // Niveau de charge
        String niveauCharge = determineNiveauCharge(scoreCharge);
        
        return UserWorkloadStatsDto.builder()
                .userId(user.getId())
                .userFullName(user.getFirstname() + " " + user.getLastname())
                .userEmail(user.getEmail())
                .userRole(user.getRole().name())
                .declarationsEnCours(declarationsEnCours)
                .declarationsNouvelles(declarationsNouvelles)
                .declarationsEnAttente(declarationsEnAttente)
                .declarationsTraiteesThisMois(declarationsTraiteesThisMois)
                .declarationsTraiteesThisYear(declarationsTraiteesThisYear)
                .tauxReussiteThisMois(tauxReussiteThisMois)
                .tauxReussiteThisYear(tauxReussiteThisYear)
                .scoreCharge(scoreCharge)
                .niveauCharge(niveauCharge)
                .build();
    }
    
    private String determineNiveauCharge(Long scoreCharge) {
        if (scoreCharge <= 5) return "FAIBLE";
        if (scoreCharge <= 15) return "MODERE";
        if (scoreCharge <= 30) return "ELEVE";
        return "CRITIQUE";
    }
    
    /**
     * Statistiques temporelles mensuelles
     */
    public List<TemporalStatsDto> getMonthlyStats(Integer year) {
        if (year == null) year = LocalDate.now().getYear();
        
        log.info("Calcul des statistiques mensuelles pour l'année {}", year);
        
        List<TemporalStatsDto> monthlyStats = new ArrayList<>();
        
        for (int month = 1; month <= 12; month++) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            
            TemporalStatsDto monthStats = calculatePeriodStats(startDate, endDate, 
                    String.format("%02d/%d", month, year));
            monthStats.setPeriode(month);
            
            monthlyStats.add(monthStats);
        }
        
        return monthlyStats;
    }
    
    /**
     * Statistiques temporelles annuelles
     */
    public List<TemporalStatsDto> getYearlyStats(Integer startYear, Integer endYear) {
        if (startYear == null) startYear = LocalDate.now().getYear() - 5;
        if (endYear == null) endYear = LocalDate.now().getYear();
        
        log.info("Calcul des statistiques annuelles de {} à {}", startYear, endYear);
        
        List<TemporalStatsDto> yearlyStats = new ArrayList<>();
        
        for (int year = startYear; year <= endYear; year++) {
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);
            
            TemporalStatsDto yearStats = calculatePeriodStats(startDate, endDate, String.valueOf(year));
            yearStats.setPeriode(year);
            
            yearlyStats.add(yearStats);
        }
        
        return yearlyStats;
    }
    
    private TemporalStatsDto calculatePeriodStats(LocalDate startDate, LocalDate endDate, String libelle) {
        // Compter les déclarations par état
        List<EtatDeclarationEnum> etatsValides = Arrays.asList(EtatDeclarationEnum.valider);
        List<EtatDeclarationEnum> etatsRefuses = Arrays.asList(EtatDeclarationEnum.refuser);
        
        Long totalDeclarations = declarationData.countByUtilisateurIdAndEtatDeclarationInAndDateBetween(
                null, Arrays.asList(EtatDeclarationEnum.values()), startDate, endDate);
        
        // Note: Ces méthodes doivent être ajoutées au repository
        Long declarationsValidees = 0L; // À implémenter
        Long declarationsRefusees = 0L; // À implémenter
        
        Double tauxValidation = totalDeclarations > 0 ? 
                (declarationsValidees.doubleValue() / totalDeclarations) * 100 : 0.0;
        
        return TemporalStatsDto.builder()
                .libellePeriode(libelle)
                .totalDeclarations(totalDeclarations)
                .declarationsValidees(declarationsValidees)
                .declarationsRefusees(declarationsRefusees)
                .tauxValidation(tauxValidation)
                .tempsMoyenTraitement(0.0) // À calculer
                .repartitionParType(new HashMap<>()) // À implémenter
                .repartitionParUtilisateur(new HashMap<>()) // À implémenter
                .build();
    }
   
    
}