package com.informatization_controle_declarations_biens.declaration_biens_control.service.bi;

import java.time.LocalDate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.HistoriqueDeclarationUserData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAssujettiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IVocabulaireData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.securite.IUtilisateurData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.AdminDashboardStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.AssujettiStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.DeclarationStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.DeclarationTrendDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.PeriodType;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.UserActivityDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.UserStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.VocabularyStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatAssujettiEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.HistoriqueDeclarationUser;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

@Service
public class AdminStatisticsService {

    private final IDeclarationData declarationData;
    private final IUtilisateurData utilisateurData;
    private final IAssujettiData assujettiData;
    private final HistoriqueDeclarationUserData historiqueData;
    private final IVocabulaireData vocabularyData;

    public AdminStatisticsService(IDeclarationData declarationData, 
                               IUtilisateurData utilisateurData,
                               IAssujettiData assujettiData,
                               HistoriqueDeclarationUserData historiqueData,IVocabulaireData vocabularyData) {
        this.declarationData = declarationData;
        this.utilisateurData = utilisateurData;
        this.assujettiData = assujettiData;
        this.historiqueData = historiqueData;
                this.vocabularyData = vocabularyData;

    }

    // ==================== STATISTIQUES UTILISATEURS ====================
    
    public UserStatsDTO getUserStatistics() {
        List<Utilisateur> allUsers = utilisateurData.findAll();
        List<Utilisateur> activeUsers = utilisateurData.findAllActiveUsers();
        List<Utilisateur> archivedUsers = utilisateurData.findAllArchivedUsers();
        
        Map<RoleEnum, Long> usersByRole = allUsers.stream()
            .collect(Collectors.groupingBy(Utilisateur::getRole, Collectors.counting()));
        
        return new UserStatsDTO(
            allUsers.size(),
            activeUsers.size(),
            archivedUsers.size(),
            usersByRole
        );
    }

    public List<UserActivityDTO> getUserActivityStats(LocalDate startDate, LocalDate endDate) {
        List<HistoriqueDeclarationUser> activities = historiqueData.findByPeriod(startDate, endDate);
        
        return activities.stream()
            .collect(Collectors.groupingBy(
                h -> h.getUtilisateur(),
                Collectors.counting()
            ))
            .entrySet().stream()
            .map(entry -> new UserActivityDTO(
                entry.getKey().getId(),
                entry.getKey().getFirstname() + " " + entry.getKey().getLastname(),
                entry.getKey().getRole().name(),
                entry.getValue()
            ))
            .sorted(Comparator.comparingLong(UserActivityDTO::getActivityCount).reversed())
            .collect(Collectors.toList());
    }

    // ==================== STATISTIQUES ASSUJETTIS ====================
    
    public AssujettiStatsDTO getAssujettiStatistics() {
        List<Assujetti> allAssujettis = assujettiData.findAll();
        List<Assujetti> activeAssujettis = assujettiData.findAssujettisExcludingEtat(EtatAssujettiEnum.Archivier);
        List<Assujetti> archivedAssujettis = assujettiData.findAssujettisWithEtat(EtatAssujettiEnum.Archivier);
        
        // Statistiques par état
        Map<EtatAssujettiEnum, Long> statsByEtat = allAssujettis.stream()
            .collect(Collectors.groupingBy(Assujetti::getEtat, Collectors.counting()));
        
        // Statistiques par période de prise de service
        Map<Integer, Long> statsByYear = allAssujettis.stream()
            .collect(Collectors.groupingBy(
                a -> a.getDatePriseDeService().getYear(),
                Collectors.counting()
            ));
        
        return new AssujettiStatsDTO(
            allAssujettis.size(),
            activeAssujettis.size(),
            archivedAssujettis.size(),
            statsByEtat,
            statsByYear
        );
    }

    // ==================== STATISTIQUES VOCABULAIRES ====================
    


public VocabularyStatsDTO getVocabularyStatistics() {
    List<Object[]> results = vocabularyData.countByType();

    Map<String, Long> termsByType = new HashMap<>();
    long total = 0;

    for (Object[] result : results) {
        String type = (String) result[0];
        Long count = (Long) result[1];
        termsByType.put(type, count);
        total += count;
    }

    return new VocabularyStatsDTO(total, termsByType);
}


    // ==================== STATISTIQUES DÉCLARATIONS ====================
    
    public DeclarationStatsDTO getDeclarationStatistics() {
        List<Declaration> allDeclarations = declarationData.findAll();
        
        // Par type de déclaration
        Map<TypeDeclarationEnum, Long> byType = allDeclarations.stream()
            .collect(Collectors.groupingBy(Declaration::getTypeDeclaration, Collectors.counting()));
        
        // Par état
        Map<EtatDeclarationEnum, Long> byEtat = allDeclarations.stream()
            .collect(Collectors.groupingBy(Declaration::getEtatDeclaration, Collectors.counting()));
        
        // Par année
        Map<Integer, Long> byYear = allDeclarations.stream()
            .collect(Collectors.groupingBy(
                d -> d.getDateDeclaration().getYear(),
                Collectors.counting()
            ));
        
        return new DeclarationStatsDTO(
            allDeclarations.size(),
            byType,
            byEtat,
            byYear
        );
    }

    public List<DeclarationTrendDTO> getDeclarationTrends(PeriodType periodType) {
        List<Declaration> declarations = declarationData.findAll();
        LocalDate now = LocalDate.now();
        
        if (periodType == PeriodType.MONTHLY) {
            return getMonthlyTrends(declarations, now);
        } else if (periodType == PeriodType.QUARTERLY) {
            return getQuarterlyTrends(declarations, now);
        } else {
            return getYearlyTrends(declarations);
        }
    }
    private List<DeclarationTrendDTO> getMonthlyTrends(List<Declaration> declarations, LocalDate now) {
    return declarations.stream()
        .collect(Collectors.groupingBy(
            d -> d.getDateDeclaration().getMonth(), // ou d.getDate().getMonth()
            Collectors.counting()
        ))
        .entrySet().stream()
        .map(e -> new DeclarationTrendDTO(e.getKey().toString(), e.getValue()))
        .collect(Collectors.toList());
}

private List<DeclarationTrendDTO> getQuarterlyTrends(List<Declaration> declarations, LocalDate now) {
    return declarations.stream()
        .collect(Collectors.groupingBy(
            d -> {
                int month = d.getDateDeclaration().getMonthValue();
                int quarter = (month - 1) / 3 + 1;
                return "Q" + quarter + " " + d.getDateDeclaration().getYear();
            },
            Collectors.counting()
        ))
        .entrySet().stream()
        .map(e -> new DeclarationTrendDTO(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
}

private List<DeclarationTrendDTO> getYearlyTrends(List<Declaration> declarations) {
    return declarations.stream()
        .collect(Collectors.groupingBy(
            d -> d.getDateDeclaration().getYear(),
            Collectors.counting()
        ))
        .entrySet().stream()
        .map(e -> new DeclarationTrendDTO(String.valueOf(e.getKey()), e.getValue()))
        .collect(Collectors.toList());
}

    
    // Méthodes similaires pour quarterly et yearly trends...

    // ==================== STATISTIQUES COMPLÈTES POUR ADMIN ====================
    
    public AdminDashboardStatsDTO getAdminDashboardStatistics() {
        return new AdminDashboardStatsDTO(
            getUserStatistics(),
            getAssujettiStatistics(),
            getVocabularyStatistics(),
            getDeclarationStatistics(),
            getDeclarationTrends(PeriodType.MONTHLY)
        );
    }
}








