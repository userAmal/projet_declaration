package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.INotificationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Notification;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle.INotificationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.AssujettiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.EmailService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.UtilisateurServiceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication; // L'import CRUCIAL
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final INotificationData notificationData;
    private final UtilisateurServiceImpl utilisateurServiceImpl;
    private final IDeclarationData declarationData;
    private final EmailService emailService;
    private final UtilisateurServiceImpl utilisateurService;


    @Override
@Transactional
public Notification createAndSendNotification(Long utilisateurId, String message, String type, Long declarationId) {
    Utilisateur utilisateur = utilisateurServiceImpl.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    Declaration declaration = declarationData.findById(declarationId)
            .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));

    Notification notification = Notification.builder()
            .message(message)
            .type(type)
            .recipient(utilisateur)
            .declaration(declaration)
            .build();

    Notification savedNotification = notificationData.save(notification);

    if (utilisateur.isStatutEmploi()) {
        // Préparation du contenu email en fonction du type de notification
        String emailSubject;
        String emailBody;

        if (type.equals("ASSIGNMENT")) {
            if (utilisateur.getRole() == RoleEnum.procureur_general) {
                if (declaration.getEtatDeclaration() == EtatDeclarationEnum.traitement) {
                    emailSubject = "Déclaration à traiter - Cour des comptes";
                    emailBody = prepareAssignmentEmailContent(utilisateur, declaration, "traitement");
                } else if (declaration.getEtatDeclaration() == EtatDeclarationEnum.jugement) {
                    emailSubject = "Déclaration prête pour jugement - Cour des comptes";
                    emailBody = prepareAssignmentEmailContent(utilisateur, declaration, "jugement");
                } else {
                    emailSubject = "Nouvelle déclaration assignée - Cour des comptes";
                    emailBody = prepareAssignmentEmailContent(utilisateur, declaration, "affectation");
                }
            } else {
                emailSubject = "Nouvelle déclaration assignée - Cour des comptes";
                emailBody = prepareAssignmentEmailContent(utilisateur, declaration, "affectation");
            }
        } else {
            // Cas par défaut pour les autres types de notifications
            emailSubject = "Nouvelle notification: " + type;
            emailBody = "<strong>Cher(e) " + utilisateur.getFirstname() + ",</strong><br><br>" +
                       message + "<br><br>" +
                       "Veuillez vous connecter à la plateforme pour plus de détails.";
        }

        Map<String, Object> variables = Map.of(
            "header", "Cour des comptes du Niger - Notification",
            "body", emailBody,
            "url", "http://localhost:4201/declarations/" + declarationId
        );

        emailService.sendEmail(
            utilisateur.getEmail(),
            emailSubject,
            "account_creation", // Même template que pour la création de compte
            variables
        );
    }

    return savedNotification;
}
private String formatRole(RoleEnum role) {
        if (role == null) {
            return "rôle non défini";
        }
        // Convertit "PROCUREUR_GENERAL" en "Procureur général"
        return Arrays.stream(role.name().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

private String prepareAssignmentEmailContent(Utilisateur utilisateur, Declaration declaration, String actionType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Utilisateur currentUser = (Utilisateur) authentication.getPrincipal();
    
    // 2. Formatage des informations avec le rôle
    String initiateurInfo = String.format("%s %s (%s)", 
            currentUser.getFirstname(),
            currentUser.getLastname(),
            formatRole(currentUser.getRole()));


    String actionDescription = switch(actionType) {
        case "traitement" -> "a son rapport provisoire prêt";
        case "jugement" -> "est prête pour jugement";
        default -> "vous a été assignée";
    };

     String nomCompletAssujetti = declaration.getAssujetti().getPrenom() + " " + declaration.getAssujetti().getNom();
    
    return "<strong>Cher(e) " + utilisateur.getFirstname() + ",</strong><br><br>" +
           "Nous vous informons que la déclaration N°" + declaration.getId() + " " + actionDescription + " (envoyée par <strong>" + initiateurInfo + "</strong>).<br><br>" +
           
           "<strong style='color: #253342; font-size: 18px;'>Détails de la déclaration</strong><br><br>" +
           "<div style='background-color: #f8f9fa; border-left: 4px solid #ffa726; padding: 18px; margin: 15px auto; width: 85%; border-radius: 4px;'>" +
           "  <div style='display: table; width: 100%;'>" +
           "    <div style='display: table-row;'>" +
           "      <div style='display: table-cell; width: 150px; padding-bottom: 12px; color: #555;'>Numéro :</div>" +
           "      <div style='display: table-cell; font-weight: bold; color: #333;'>" + declaration.getId() + "</div>" +
           "    </div>" +
           "    <div style='display: table-row;'>" +
           "      <div style='display: table-cell; width: 150px; color: #555;'>Assujetti :</div>" +
           "      <div style='display: table-cell; font-weight: bold;'>" +
           "        <span style='color: #333; background-color: #fff; padding: 4px 10px; border-radius: 3px; border: 1px solid #ddd;'>" + 
                      nomCompletAssujetti + 
                   "</span>" +
           "      </div>" +
           "    </div>" +
           "  </div>" +
           "</div><br>" +
           
           "Veuillez cliquer sur le lien ci-dessous pour accéder à la plateforme :<br>";
}
@Transactional
public void notifyTransferDeclarations(Utilisateur source, Utilisateur cible, List<Declaration> declarations) {
    if (declarations.isEmpty()) return;

    // 1. Notification concise dans l'application
    String notificationMessage = String.format(
        "📩 %s %s vous a transféré %d déclaration(s)",
        source.getFirstname(),
        source.getLastname(),
        declarations.size()
    );

    Notification notification = Notification.builder()
        .message(notificationMessage)
        .type("TRANSFER")
        .recipient(cible)
        .declaration(declarations.get(0)) // Référence à la première déclaration
        .isRead(false)
        .build();

    notificationData.save(notification);

    // 2. Email inchangé (identique à votre version actuelle)
    if (cible.isStatutEmploi()) {
        String emailSubject = "Nouvelles déclarations transférées - Cour des comptes";
        
        StringBuilder declarationsList = new StringBuilder();
        declarations.forEach(decl -> {
            declarationsList.append("<li style='margin-bottom: 10px;'>")
                .append("Déclaration #").append(decl.getId())
                .append(" - ").append(decl.getAssujetti().getNom()).append(" ").append(decl.getAssujetti().getPrenom())
                .append(" (").append(decl.getEtatDeclaration().toString().replace("_", " ")).append(")")
                .append("</li>");
        });

        String emailBody = "<strong>Cher(e) " + cible.getFirstname() + ",</strong><br><br>" +
            "Vous avez reçu " + declarations.size() + " nouvelle(s) déclaration(s) :<br><br>" +
            "<ul style='list-style-type: none; padding-left: 0;'>" + declarationsList.toString() + "</ul><br>" +
            "Transféré par : <strong>" + formatRole(source.getRole())+" ( "+ source.getFirstname() + " " + source.getLastname()+" )." + "</strong><br><br>" +
            "E-mail: " + source.getEmail()+"<br>"+
            "Veuillez vous connecter à la plateforme pour les traiter :<br>";

        Map<String, Object> variables = Map.of(
            "header", "Transfert de déclarations - Cour des comptes",
            "body", emailBody,
            "url", "http://localhost:4201/declarations"
        );

        emailService.sendEmail(
            cible.getEmail(),
            emailSubject,
            "account_creation",
            variables
        );
    }
}

    @Override
    public List<Notification> getNotificationsByUtilisateur(Long utilisateurId) {
        return notificationData.findByRecipientIdOrderByCreatedAtDesc(utilisateurId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long utilisateurId) {
        return notificationData.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(utilisateurId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationData.markAsRead(notificationId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long utilisateurId) {
        notificationData.markAllAsRead(utilisateurId);
    }

    @Override
    public int getUnreadCount(Long utilisateurId) {
        return notificationData.countByRecipientIdAndIsReadFalse(utilisateurId);
    }
}