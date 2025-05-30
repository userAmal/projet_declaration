package com.informatization_controle_declarations_biens.declaration_biens_control.service.bi;

import java.time.LocalDate;
import java.util.Arrays;
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
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.UserActivityDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.UserStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat.VocabularyStatsDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
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
                               HistoriqueDeclarationUserData historiqueData,
                               IVocabulaireData vocabularyData) {
        this.declarationData = declarationData;
        this.utilisateurData = utilisateurData;
        this.assujettiData = assujettiData;
        this.historiqueData = historiqueData;
        this.vocabularyData = vocabularyData;
    }

    // ==================== STATISTIQUES UTILISATEURS ====================
    
    public UserStatsDTO getUserStatistics() {
        try {
            List<Utilisateur> allUsers = utilisateurData.findAll();
            List<Utilisateur> activeUsers = utilisateurData.findAllActiveUsers();
            List<Utilisateur> archivedUsers = utilisateurData.findAllArchivedUsers();
            
            // Filtrer les utilisateurs avec des IDs valides
            allUsers = allUsers.stream()
                .filter(user -> user.getId() != null && user.getId() > 0)
                .collect(Collectors.toList());
            
            Map<RoleEnum, Long> usersByRole = allUsers.stream()
                .collect(Collectors.groupingBy(Utilisateur::getRole, Collectors.counting()));
            
            return new UserStatsDTO(
                allUsers.size(),
                activeUsers.size(),
                archivedUsers.size(),
                usersByRole
            );
        } catch (Exception e) {
            return new UserStatsDTO(0, 0, 0, new HashMap<>());
        }
    }

    public List<UserActivityDTO> getUserActivityStats(LocalDate startDate, LocalDate endDate) {
        try {
            List<HistoriqueDeclarationUser> activities = historiqueData.findByPeriod(startDate, endDate);
            
            return activities.stream()
                .filter(h -> h.getUtilisateur() != null && 
                           h.getUtilisateur().getId() != null && 
                           h.getUtilisateur().getId() > 0) // Filtrer les utilisateurs invalides
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
        } catch (Exception e) {
            return List.of();
        }
    }

    // ==================== STATISTIQUES ASSUJETTIS ====================
    
    public AssujettiStatsDTO getAssujettiStatistics() {
        try {
            List<Assujetti> allAssujettis = assujettiData.findAll();
            List<Assujetti> activeAssujettis = assujettiData.findAssujettisExcludingEtat(EtatAssujettiEnum.STOP);
            List<Assujetti> archivedAssujettis = assujettiData.findAssujettisWithEtat(EtatAssujettiEnum.STOP);
            
            // Statistiques par état
            Map<EtatAssujettiEnum, Long> statsByEtat = allAssujettis.stream()
                .filter(a -> a.getEtat() != null) // Vérifier que l'état n'est pas null
                .collect(Collectors.groupingBy(Assujetti::getEtat, Collectors.counting()));
            
            // Statistiques par période de prise de service
            Map<Integer, Long> statsByYear = allAssujettis.stream()
                .filter(a -> a.getDatePriseDeService() != null) // Vérifier que la date n'est pas null
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
        } catch (Exception e) {
            return new AssujettiStatsDTO(0, 0, 0, new HashMap<>(), new HashMap<>());
        }
    }

    // ==================== STATISTIQUES VOCABULAIRES ====================
    
    public VocabularyStatsDTO getVocabularyStatistics() {
        try {
            List<Object[]> results = vocabularyData.countByType();

            Map<String, Long> termsByType = new HashMap<>();
            long total = 0;

            for (Object[] result : results) {
                String type = (String) result[0];
                Long count = (Long) result[1];
                if (type != null && count != null) {
                    termsByType.put(type, count);
                    total += count;
                }
            }

            return new VocabularyStatsDTO(total, termsByType);
        } catch (Exception e) {
            return new VocabularyStatsDTO(0, new HashMap<>());
        }
    }
public DeclarationStatsDTO getDeclarationStatistics() {
    try {
        // Debug: Log the raw counts
        System.out.println("Fetching declaration statistics...");
        
        // Get total count
        Long totalCount = declarationData.countAllDeclarations();
        System.out.println("Total declarations count: " + totalCount);
        
        // Process type statistics
        List<Object[]> typeResults = declarationData.countByType();
        System.out.println("Type results: " + Arrays.deepToString(typeResults.toArray()));
        
        Map<TypeDeclarationEnum, Long> byType = new HashMap<>();
        for (Object[] result : typeResults) {
            try {
                Integer typeValue = ((Number) result[0]).intValue();
                Long count = ((Number) result[1]).longValue();
                TypeDeclarationEnum type = TypeDeclarationEnum.values()[typeValue];
                byType.put(type, count);
            } catch (Exception e) {
                System.err.println("Error processing type result: " + Arrays.toString(result));
                e.printStackTrace();
            }
        }
        
        // Process etat statistics
        List<Object[]> etatResults = declarationData.countByEtat();
        System.out.println("Etat results: " + Arrays.deepToString(etatResults.toArray()));
        
        Map<EtatDeclarationEnum, Long> byEtat = new HashMap<>();
        for (Object[] result : etatResults) {
            try {
                Integer etatValue = ((Number) result[0]).intValue();
                Long count = ((Number) result[1]).longValue();
                
                // Map database values to enum
                EtatDeclarationEnum etat;
                switch (etatValue) {
                    case 0: etat = EtatDeclarationEnum.nouveau; break;
                    case 1: etat = EtatDeclarationEnum.en_cours; break;
                    case 2: etat = EtatDeclarationEnum.traitement; break;
                    case 3: etat = EtatDeclarationEnum.jugement; break;
                    case 4: etat = EtatDeclarationEnum.valider; break;
                    case 5: etat = EtatDeclarationEnum.No_declaré; break;
                    case 6: etat = EtatDeclarationEnum.refuser; break;

                    default:
                        System.out.println("Unknown etat value: " + etatValue);
                        continue; // Skip unknown values
                }
                byEtat.put(etat, count);
            } catch (Exception e) {
                System.err.println("Error processing etat result: " + Arrays.toString(result));
                e.printStackTrace();
            }
        }
        
        // Process year statistics
        List<Object[]> yearResults = declarationData.countByYear();
        System.out.println("Year results: " + Arrays.deepToString(yearResults.toArray()));
        
        Map<Integer, Long> byYear = new HashMap<>();
        for (Object[] result : yearResults) {
            try {
                Integer year = ((Number) result[0]).intValue();
                Long count = ((Number) result[1]).longValue();
                byYear.put(year, count);
            } catch (Exception e) {
                System.err.println("Error processing year result: " + Arrays.toString(result));
                e.printStackTrace();
            }
        }
        
        // Debug: Log final maps
        System.out.println("Final byType map: " + byType);
        System.out.println("Final byEtat map: " + byEtat);
        System.out.println("Final byYear map: " + byYear);
        
        return new DeclarationStatsDTO(
            totalCount != null ? totalCount : 0, 
            byType, 
            byEtat, 
            byYear
        );
    } catch (Exception e) {
        System.err.println("Error in getDeclarationStatistics: " + e.getMessage());
        e.printStackTrace();
        return new DeclarationStatsDTO(0, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }
}
  
    
   
    // ==================== STATISTIQUES COMPLÈTES POUR ADMIN ====================
    
    public AdminDashboardStatsDTO getAdminDashboardStatistics() {
        try {
            return new AdminDashboardStatsDTO(
                getUserStatistics(),
                getAssujettiStatistics(),
                getVocabularyStatistics(),
                getDeclarationStatistics()
            );
        } catch (Exception e) {
            return new AdminDashboardStatsDTO(
                new UserStatsDTO(0, 0, 0, new HashMap<>()),
                new AssujettiStatsDTO(0, 0, 0, new HashMap<>(), new HashMap<>()),
                new VocabularyStatsDTO(0, new HashMap<>()),
                new DeclarationStatsDTO(0, new HashMap<>(), new HashMap<>(), new HashMap<>())

            );
        }
    }
    
}