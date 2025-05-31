package com.informatization_controle_declarations_biens.declaration_biens_control.service.bi;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.IConclusionData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.ICommentaireGeneriqueData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.IRapportData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.CommentaireGenerique;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Conclusion;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de statistiques spécialement conçu pour l'Avocat Général
 * Fournit des métriques pertinentes pour ses responsabilités :
 * - Vérification des déclarations
 * - Génération des conclusions (acceptation/refus)
 * - Suivi des commentaires
 */
@Service
public class AvocatGeneralStatisticsService {

    private final IDeclarationData declarationData;
    private final IConclusionData conclusionData;
    private final ICommentaireGeneriqueData commentaireData;
    private final IRapportData rapportData;

    public AvocatGeneralStatisticsService(IDeclarationData declarationData,
                                        IConclusionData conclusionData,
                                        ICommentaireGeneriqueData commentaireData,
                                        IRapportData rapportData) {
        this.declarationData = declarationData;
        this.conclusionData = conclusionData;
        this.commentaireData = commentaireData;
        this.rapportData = rapportData;
    }

    // ==================== STATISTIQUES PRINCIPALES ====================

    /**
     * Statistiques globales pour l'avocat général
     */
    public AvocatGeneralGlobalStatsDTO getGlobalStats(Long avocatGeneralId) {
        List<Declaration> declarationsAssignees = declarationData.findByUtilisateurId(avocatGeneralId);
        List<Conclusion> conclusions = conclusionData.findByUtilisateurId(avocatGeneralId);
        List<CommentaireGenerique> commentaires = commentaireData.findByUtilisateurId(avocatGeneralId);

        long totalDeclarations = declarationsAssignees.size();
        long conclusionsGenerees = conclusions.size();
        long commentairesAjoutes = commentaires.size();
        
        // Calcul du taux de traitement
        double tauxTraitement = totalDeclarations > 0 ? 
            (conclusionsGenerees * 100.0 / totalDeclarations) : 0.0;

        // Déclarations en attente de conclusion
        long enAttenteConclusion = declarationsAssignees.stream()
            .filter(d -> conclusions.stream()
                .noneMatch(c -> c.getDeclaration().getId().equals(d.getId())))
            .count();

        return new AvocatGeneralGlobalStatsDTO(
            totalDeclarations,
            conclusionsGenerees,
            enAttenteConclusion,
            commentairesAjoutes,
            tauxTraitement
        );
    }

    /**
     * Statistiques des conclusions (acceptation/refus)
     */
    public ConclusionStatsDTO getConclusionStats(Long avocatGeneralId) {
        List<Conclusion> conclusions = conclusionData.findByUtilisateurId(avocatGeneralId);

        long totalConclusions = conclusions.size();
long acceptations = conclusions.stream()
    .filter(Conclusion::isEstAcceptation)  // Utilisez le getter existant (lombok génère isEstAcceptation)
    .count();
long refus = conclusions.stream()
    .filter(c -> !c.isEstAcceptation())   // Utilisez la négation du getter existant
    .count();

        double tauxAcceptation = totalConclusions > 0 ? 
            (acceptations * 100.0 / totalConclusions) : 0.0;
        double tauxRefus = totalConclusions > 0 ? 
            (refus * 100.0 / totalConclusions) : 0.0;

        return new ConclusionStatsDTO(
            totalConclusions,
            acceptations,
            refus,
            tauxAcceptation,
            tauxRefus
        );
    }

    
    /**
     * Analyse des déclarations par type pour l'avocat général
     */
    public List<DeclarationTypeAnalysisDTO> getAnalyseParType(Long avocatGeneralId) {
        List<Declaration> declarations = declarationData.findByUtilisateurId(avocatGeneralId);
        List<Conclusion> conclusions = conclusionData.findByUtilisateurId(avocatGeneralId);

        Map<TypeDeclarationEnum, List<Declaration>> declarationsByType = 
            declarations.stream().collect(Collectors.groupingBy(Declaration::getTypeDeclaration));

        return declarationsByType.entrySet().stream()
            .map(entry -> {
                TypeDeclarationEnum type = entry.getKey();
                List<Declaration> typeDeclarations = entry.getValue();
                
long acceptations = conclusions.stream()
    .filter(Conclusion::isEstAcceptation)  // Utilisez le getter existant
    .filter(c -> typeDeclarations.stream()
        .anyMatch(d -> d.getId().equals(c.getDeclaration().getId())))
    .count();

long refus = conclusions.stream()
    .filter(c -> !c.isEstAcceptation())   // Utilisez la négation du getter existant
    .filter(c -> typeDeclarations.stream()
        .anyMatch(d -> d.getId().equals(c.getDeclaration().getId())))
    .count();

                double tauxAcceptation = typeDeclarations.size() > 0 ? 
                    (acceptations * 100.0 / typeDeclarations.size()) : 0.0;

                return new DeclarationTypeAnalysisDTO(
                    type.name(),
                    typeDeclarations.size(),
                    acceptations,
                    refus,
                    tauxAcceptation
                );
            })
            .collect(Collectors.toList());
    }

    
   public ChargeTravailtDTO getChargeTravail(Long avocatGeneralId) {
    List<Declaration> declarations = declarationData.findByUtilisateurId(avocatGeneralId);
    List<Conclusion> conclusions = conclusionData.findByUtilisateurId(avocatGeneralId);

    // Déclarations en cours de traitement
    long enCours = declarations.stream()
        .filter(d -> d.getEtatDeclaration() == EtatDeclarationEnum.en_cours )
        .filter(d -> conclusions.stream()
            .noneMatch(c -> c.getDeclaration().getId().equals(d.getId())))
        .count();


    return new ChargeTravailtDTO(enCours);
}
    // ==================== DASHBOARD COMPLET ====================

    /**
     * Dashboard complet pour l'avocat général
     */
    public Map<String, Object> getDashboardComplet(Long avocatGeneralId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("statistiquesGlobales", getGlobalStats(avocatGeneralId));
        dashboard.put("conclusionStats", getConclusionStats(avocatGeneralId));
        dashboard.put("analyseParType", getAnalyseParType(avocatGeneralId));
        dashboard.put("chargeTravail", getChargeTravail(avocatGeneralId));
        
        return dashboard;
    }

   

    // ==================== CLASSES DTO INTERNES ====================

    public static class AvocatGeneralGlobalStatsDTO {
        private long totalDeclarations;
        private long conclusionsGenerees;
        private long enAttenteConclusion;
        private long commentairesAjoutes;
        private double tauxTraitement;

        public AvocatGeneralGlobalStatsDTO(long totalDeclarations, long conclusionsGenerees, 
                                         long enAttenteConclusion, long commentairesAjoutes, double tauxTraitement) {
            this.totalDeclarations = totalDeclarations;
            this.conclusionsGenerees = conclusionsGenerees;
            this.enAttenteConclusion = enAttenteConclusion;
            this.commentairesAjoutes = commentairesAjoutes;
            this.tauxTraitement = tauxTraitement;
        }

        // Getters
        public long getTotalDeclarations() { return totalDeclarations; }
        public long getConclusionsGenerees() { return conclusionsGenerees; }
        public long getEnAttenteConclusion() { return enAttenteConclusion; }
        public long getCommentairesAjoutes() { return commentairesAjoutes; }
        public double getTauxTraitement() { return tauxTraitement; }
    }

    public static class ConclusionStatsDTO {
        private long totalConclusions;
        private long acceptations;
        private long refus;
        private double tauxAcceptation;
        private double tauxRefus;

        public ConclusionStatsDTO(long totalConclusions, long acceptations, long refus, 
                                double tauxAcceptation, double tauxRefus) {
            this.totalConclusions = totalConclusions;
            this.acceptations = acceptations;
            this.refus = refus;
            this.tauxAcceptation = tauxAcceptation;
            this.tauxRefus = tauxRefus;
        }

        // Getters
        public long getTotalConclusions() { return totalConclusions; }
        public long getAcceptations() { return acceptations; }
        public long getRefus() { return refus; }
        public double getTauxAcceptation() { return tauxAcceptation; }
        public double getTauxRefus() { return tauxRefus; }
    }

    public static class PerformanceTemporelleDTO {
        private double tempsMoyenTraitement;
        private double tauxRespectDelais;
        private long retardsActuels;

        public PerformanceTemporelleDTO(double tempsMoyenTraitement, double tauxRespectDelais, long retardsActuels) {
            this.tempsMoyenTraitement = tempsMoyenTraitement;
            this.tauxRespectDelais = tauxRespectDelais;
            this.retardsActuels = retardsActuels;
        }

        // Getters
        public double getTempsMoyenTraitement() { return tempsMoyenTraitement; }
        public double getTauxRespectDelais() { return tauxRespectDelais; }
        public long getRetardsActuels() { return retardsActuels; }
    }

    public static class DeclarationTypeAnalysisDTO {
        private String typeDeclaration;
        private long total;
        private long acceptations;
        private long refus;
        private double tauxAcceptation;

        public DeclarationTypeAnalysisDTO(String typeDeclaration, long total, long acceptations, long refus, double tauxAcceptation) {
            this.typeDeclaration = typeDeclaration;
            this.total = total;
            this.acceptations = acceptations;
            this.refus = refus;
            this.tauxAcceptation = tauxAcceptation;
        }

        // Getters
        public String getTypeDeclaration() { return typeDeclaration; }
        public long getTotal() { return total; }
        public long getAcceptations() { return acceptations; }
        public long getRefus() { return refus; }
        public double getTauxAcceptation() { return tauxAcceptation; }
    }

    public static class EvolutionMensuelleDTO {
        private String[] mois;
        private long[] acceptations;
        private long[] refus;

        public EvolutionMensuelleDTO(String[] mois, long[] acceptations, long[] refus) {
            this.mois = mois;
            this.acceptations = acceptations;
            this.refus = refus;
        }

        // Getters
        public String[] getMois() { return mois; }
        public long[] getAcceptations() { return acceptations; }
        public long[] getRefus() { return refus; }
    }

    public static class CommentaireAnalysisDTO {
        private long totalCommentaires;
        private long declarationsCommentees;
        private double moyenneCommentairesParDeclaration;
        private Map<Month, Long> commentairesParMois;

        public CommentaireAnalysisDTO(long totalCommentaires, long declarationsCommentees, 
                                    double moyenneCommentairesParDeclaration, Map<Month, Long> commentairesParMois) {
            this.totalCommentaires = totalCommentaires;
            this.declarationsCommentees = declarationsCommentees;
            this.moyenneCommentairesParDeclaration = moyenneCommentairesParDeclaration;
            this.commentairesParMois = commentairesParMois;
        }

        // Getters
        public long getTotalCommentaires() { return totalCommentaires; }
        public long getDeclarationsCommentees() { return declarationsCommentees; }
        public double getMoyenneCommentairesParDeclaration() { return moyenneCommentairesParDeclaration; }
        public Map<Month, Long> getCommentairesParMois() { return commentairesParMois; }
    }

    public static class ChargeTravailtDTO {
        private long enCours;
;

        public ChargeTravailtDTO( long enCours) {
            this.enCours = enCours;

        }

        // Getters
        public long getEnCours() { return enCours; }

    }

    public static class ComparaisonPerformanceDTO {
        private double monTauxAcceptation;
        private double tauxAcceptationMoyen;
        private double monTempsMoyen;
        private double tempsMoyenGlobal;
        private double monTauxRespectDelais;
        private double tauxRespectDelaisMoyen;

        public ComparaisonPerformanceDTO(double monTauxAcceptation, double tauxAcceptationMoyen, 
                                       double monTempsMoyen, double tempsMoyenGlobal,
                                       double monTauxRespectDelais, double tauxRespectDelaisMoyen) {
            this.monTauxAcceptation = monTauxAcceptation;
            this.tauxAcceptationMoyen = tauxAcceptationMoyen;
            this.monTempsMoyen = monTempsMoyen;
            this.tempsMoyenGlobal = tempsMoyenGlobal;
            this.monTauxRespectDelais = monTauxRespectDelais;
            this.tauxRespectDelaisMoyen = tauxRespectDelaisMoyen;
        }

        // Getters
        public double getMonTauxAcceptation() { return monTauxAcceptation; }
        public double getTauxAcceptationMoyen() { return tauxAcceptationMoyen; }
        public double getMonTempsMoyen() { return monTempsMoyen; }
        public double getTempsMoyenGlobal() { return tempsMoyenGlobal; }
        public double getMonTauxRespectDelais() { return monTauxRespectDelais; }
        public double getTauxRespectDelaisMoyen() { return tauxRespectDelaisMoyen; }
    }
}