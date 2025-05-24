package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.INotificationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Notification;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle.INotificationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.EmailService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.UtilisateurServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final INotificationData notificationData;
    private final UtilisateurServiceImpl utilisateurServiceImpl;
    private final IDeclarationData declarationData;
    private final EmailService emailService;

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
            Map<String, Object> variables = Map.of(
                "nomComplet", utilisateur.getFirstname() + " " + utilisateur.getLastname(),
                "message", message
            );

            emailService.sendEmail(
                utilisateur.getEmail(),
                "Nouvelle notification: " + type,
                "account_creation",
                variables
            );
        }

        return savedNotification;
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