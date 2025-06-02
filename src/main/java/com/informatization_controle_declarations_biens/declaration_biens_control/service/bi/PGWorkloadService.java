package com.informatization_controle_declarations_biens.declaration_biens_control.service.bi;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.securite.IUtilisateurData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.pgStat.PGWorkloadDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PGWorkloadService {
    
    private final IDeclarationData declarationData;
    private final IUtilisateurData utilisateurData;

    
    public PGWorkloadService(IDeclarationData declarationData, 
                           IUtilisateurData utilisateurData) {
        this.declarationData = declarationData;
        this.utilisateurData = utilisateurData;

    }
    
    /**
     * Calcule la charge de travail pour tous les PG
     */
    public List<PGWorkloadDto> calculateAllPGWorkloads() {
        List<Utilisateur> procureursGeneraux = utilisateurData.findByRole(RoleEnum.procureur_general);
        
        return procureursGeneraux.stream()
                .map(this::calculatePGWorkload) 
                .sorted(Comparator.comparingLong(PGWorkloadDto::getTotalCharge))
                .collect(Collectors.toList());
    }
    
    /**
     * Calcule la charge de travail pour un PG spécifique
     */
    public PGWorkloadDto calculatePGWorkload(Long pgId) {
        Utilisateur pg = utilisateurData.findById(pgId)
            .orElseThrow(() -> new EntityNotFoundException("PG non trouvé"));
        
        if (pg.getRole() != RoleEnum.procureur_general) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un Procureur Général");
        }
        
        return calculatePGWorkload(pg);
    }
    
    private PGWorkloadDto calculatePGWorkload(Utilisateur pg) {
        // Déclarations en cours de traitement
        List<EtatDeclarationEnum> etatsEnCours = Arrays.asList(
            EtatDeclarationEnum.en_cours,
            EtatDeclarationEnum.traitement,
            EtatDeclarationEnum.jugement
        );
        
        long declarationsEnCours = declarationData.countByUtilisateurIdAndEtatDeclarationIn(
            pg.getId(), etatsEnCours);
        
        // Déclarations nouvelles assignées
        long declarationsNouvelles = declarationData.countByUtilisateurIdAndEtatDeclaration(
            pg.getId(), EtatDeclarationEnum.nouveau);
        
        // Déclarations non déclarées à traiter
        long declarationsNonDeclarees = declarationData.countByUtilisateurIdAndEtatDeclaration(
            pg.getId(), EtatDeclarationEnum.No_declaré);
        
        // Déclarations terminées ce mois
        LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
        long declarationsTerminees = declarationData.countByUtilisateurIdAndEtatDeclarationInAndDateBetween(
            pg.getId(), 
            Arrays.asList(EtatDeclarationEnum.valider, EtatDeclarationEnum.refuser),
            debutMois, 
            LocalDate.now()
        );
        
        // Score de charge (pondération)
        long scoreCharge = (declarationsEnCours * 3) + 
                          (declarationsNouvelles * 2) + 
                          (declarationsNonDeclarees * 4);
        
        return PGWorkloadDto.builder()
            .pgId(pg.getId())
            .pgNom(pg.getFirstname() + " " + pg.getLastname())
            .pgEmail(pg.getEmail())
            .declarationsEnCours(declarationsEnCours)
            .declarationsNouvelles(declarationsNouvelles)
            .declarationsNonDeclarees(declarationsNonDeclarees)
            .declarationsTerminees(declarationsTerminees)
            .totalCharge(scoreCharge)
            .disponibilite(calculateDisponibilite(scoreCharge))
            .build();
    }
    
    private String calculateDisponibilite(long scoreCharge) {
        if (scoreCharge <= 10) return "DISPONIBLE";
        if (scoreCharge <= 25) return "MODERE";
        if (scoreCharge <= 40) return "CHARGE";
        return "SURCHARGE";
    }
    
    /**
     * Trouve le PG le moins chargé pour assignment automatique
     */
    public Utilisateur findLeastLoadedPG() {
        List<PGWorkloadDto> workloads = calculateAllPGWorkloads();
        
        if (workloads.isEmpty()) {
            throw new RuntimeException("Aucun Procureur Général disponible");
        }
        
        PGWorkloadDto leastLoaded = workloads.get(0);
        log.info("PG le moins chargé: {} avec une charge de {}", 
                leastLoaded.getPgNom(), leastLoaded.getTotalCharge());
        
        return utilisateurData.findById(leastLoaded.getPgId())
            .orElseThrow(() -> new RuntimeException("PG non trouvé"));
    }
}

