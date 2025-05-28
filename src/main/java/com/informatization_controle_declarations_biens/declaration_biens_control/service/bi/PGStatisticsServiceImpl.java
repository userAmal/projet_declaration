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
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.AdvisorPerformance;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.DecisionStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.DeclarationStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.ReportStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.TemporalData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.TemporalStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.WorkflowStats;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.bi.PGStatisticsService;

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
        long total = declarationData.count();
        long initiales = declarationData.countByTypeDeclaration(TypeDeclarationEnum.Initiale);
        long misesAJour = declarationData.countByTypeDeclaration(TypeDeclarationEnum.Mise_à_jour);
        
        return new DeclarationStats(total, initiales, misesAJour);
    }

    @Override
    public ReportStats getReportStats() {
        List<Rapport> rapports = rapportData.findAll();
        
        long provisoires = rapports.stream()
                .filter(r -> r.getType() == Rapport.Type.PROVISOIRE)
                .count();
                
        long definitifs = rapports.stream()
                .filter(r -> r.getType() == Rapport.Type.DEFINITIF)
                .count();
                
        long enRetard = historiqueData.findActiveAffectations().stream()
                .filter(h -> ChronoUnit.DAYS.between(h.getDateAffectation(), LocalDate.now()) > 30)
                .count();
                
        return new ReportStats(provisoires, definitifs, enRetard);
    }

    @Override
    public DecisionStats getDecisionStats() {
        List<Rapport> rapports = rapportData.findByType(Rapport.Type.DEFINITIF);
        
        long acceptees = rapports.stream()
                .filter(r -> r.getDecision() != null && r.getDecision())
                .count();
                
        long refusees = rapports.stream()
                .filter(r -> r.getDecision() != null && !r.getDecision())
                .count();
                
        return new DecisionStats(acceptees, refusees);
    }

    @Override
    public List<AdvisorPerformance> getAdvisorsPerformance() {
        List<Utilisateur> conseillers = utilisateurData.findByRole(RoleEnum.conseiller_rapporteur);
        
        return conseillers.stream().map(conseiller -> {
            List<Declaration> declarations = declarationData.findByUtilisateurId(conseiller.getId());
            List<Rapport> rapports = rapportData.findByUtilisateurId(conseiller.getId());
            
            long acceptees = rapports.stream()
                    .filter(r -> r.getType() == Rapport.Type.DEFINITIF && 
                               r.getDecision() != null && r.getDecision())
                    .count();
                    
            double tauxAcceptation = rapports.isEmpty() ? 0 : (acceptees * 100.0 / rapports.size());
            
            double tempsMoyen = declarations.stream()
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
        }).collect(Collectors.toList());
    }

    @Override
    public TemporalStats getTemporalStats(String period) {
        List<Declaration> declarations = declarationData.findAll();
        List<Rapport> rapports = rapportData.findAll();
        
        if ("monthly".equals(period)) {
            Map<String, TemporalData> monthlyData = new LinkedHashMap<>();
            
            for (int i = 1; i <= 12; i++) {
                Month month = Month.of(i);
                String monthName = month.getDisplayName(TextStyle.SHORT, Locale.FRENCH);
                
                final int monthNum = i;
                long decCount = declarations.stream()
                        .filter(d -> d.getDateDeclaration().getMonthValue() == monthNum)
                        .count();
                        
                long rapCount = rapports.stream()
                        .filter(r -> r.getDateCreation().getMonthValue() == monthNum)
                        .count();
                        
                monthlyData.put(monthName, new TemporalData(decCount, rapCount));
            }
            
            return new TemporalStats("monthly", monthlyData);
        }
        
        // Autres périodes (trimestrielle, annuelle)...
        return null;
    }

    @Override
    public WorkflowStats getWorkflowStats() {
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
                
        return new WorkflowStats(nouveaux, enCours, termines);
    }
}