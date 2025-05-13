package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Notification;

import java.util.List;

public interface INotificationService {
    
    Notification createAndSendNotification(Long utilisateurId, String message, String type, Long declarationId);

    List<Notification> getNotificationsByUtilisateur(Long utilisateurId);

    List<Notification> getUnreadNotifications(Long utilisateurId);

    void markAsRead(Long notificationId);

    void markAllAsRead(Long utilisateurId);

    int getUnreadCount(Long utilisateurId);
}
