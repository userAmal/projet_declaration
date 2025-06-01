package com.informatization_controle_declarations_biens.declaration_biens_control.service.bi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDeclarationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.control.NotificationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.EmailService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeclarationAssignmentService {
    
    private final PGWorkloadService pgWorkloadService;
    private final IDeclarationService declarationService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    
    public DeclarationAssignmentService(PGWorkloadService pgWorkloadService,
                                      IDeclarationService declarationService,
                                      NotificationService notificationService,
                                      EmailService emailService) {
        this.pgWorkloadService = pgWorkloadService;
        this.declarationService = declarationService;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }
    
    /**
     * Assigne automatiquement une déclaration au PG le moins chargé
     */
    @Transactional
    public void assignDeclarationToPG(Long declarationId, String motif) {
        Declaration declaration = declarationService.findById(declarationId)
            .orElseThrow(() -> new EntityNotFoundException("Déclaration non trouvée"));
        
        // Trouver le PG le moins chargé
        Utilisateur pgLeMoinsCharge = pgWorkloadService.findLeastLoadedPG();
        
        // Assigner la déclaration
        declaration.setUtilisateur(pgLeMoinsCharge);
        
        // Changer l'état selon le contexte
        if (declaration.getEtatDeclaration() == EtatDeclarationEnum.nouveau) {
            declaration.setEtatDeclaration(EtatDeclarationEnum.en_cours);
        } else if (declaration.getEtatDeclaration() == EtatDeclarationEnum.No_declaré) {
            // Reste en No_declaré mais assigné pour traitement de l'amende
        }
        
        Declaration savedDeclaration = declarationService.save(declaration);
        
        // Créer la notification
        String message = String.format(
            "Nouvelle déclaration assignée automatiquement - %s (N°: %d). Motif: %s",
            declaration.getEtatDeclaration().toString(),
            declarationId,
            motif
        );
        
        notificationService.createAndSendNotification(
            pgLeMoinsCharge.getId(),
            message,
            "AUTO_ASSIGNMENT",
            declarationId
        );
        
        // Envoyer email de notification
        sendEmailNotificationToPG(pgLeMoinsCharge, savedDeclaration, motif);
        
        log.info("Déclaration {} assignée automatiquement au PG {} ({})", 
                declarationId, pgLeMoinsCharge.getEmail(), motif);
    }
    
    private void sendEmailNotificationToPG(Utilisateur pg, Declaration declaration, String motif) {
        try {
            Map<String, Object> emailVariables = Map.of(
                "pgNom", pg.getFirstname() + " " + pg.getLastname(),
                "declarationId", declaration.getId(),
                "assujettiNom", declaration.getAssujetti().getNom() + " " + 
                              declaration.getAssujetti().getPrenom(),
                "etatDeclaration", declaration.getEtatDeclaration().toString(),
                "motif", motif,
                "dateAssignation", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
            
            emailService.sendEmail(
                pg.getEmail(),
                "Nouvelle déclaration assignée - " + declaration.getId(),
                "notification_assignment_pg", // Template à créer
                emailVariables
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email au PG {}: {}", pg.getEmail(), e.getMessage());
        }
    }
}
