package com.informatization_controle_declarations_biens.declaration_biens_control.service.securite;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.securite.IUtilisateurData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@Transactional
public class UtilisateurServiceImpl implements IUtilisateurService {
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurServiceImpl.class); // Déclaration statique du Logger

    private final IUtilisateurData utilisateurData;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UtilisateurServiceImpl(
        IUtilisateurData utilisateurData,
        @Lazy PasswordEncoder passwordEncoder

    ) {
        this.utilisateurData = utilisateurData;
        this.passwordEncoder = passwordEncoder;

    }
    @Autowired
    private EmailService emailService; 
    
    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        // Génère un mot de passe SEULEMENT pour les nouveaux utilisateurs
        if (utilisateur.getId() == null) { 
            String generatedPassword = generateSecurePassword();
            String encodedPassword = passwordEncoder.encode(generatedPassword);
            utilisateur.setPassword(encodedPassword);
    
            // if (utilisateur.getRole() != RoleEnum.administrateur) {
            //     utilisateur.setFirstLogin(true);
            // }
    
            // Envoi d'email uniquement pour les nouveaux comptes
            String subject = "Création de votre compte";
            String message = String.format(
                "Bonjour %s,\n\nVotre compte a été créé avec succès.\n\nLogin: %s\nMot de passe: %s\n\nVeuillez changer votre mot de passe après votre première connexion.\n\nCordialement,\nL'équipe de support.",
                utilisateur.getFirstname(),
                utilisateur.getEmail(),
                generatedPassword
            );
            emailService.sendEmail(utilisateur.getEmail(), subject, message);
        }
    
        return utilisateurData.save(utilisateur);
    }
    
    
    
    public void changePassword(String email, String newPassword) {
        Utilisateur utilisateur = utilisateurData.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    
        utilisateur.setPassword(passwordEncoder.encode(newPassword)); // Hachage du mot de passe
        utilisateur.setFirstLogin(false);
        utilisateurData.save(utilisateur);
        utilisateurData.flush();
    }
    
    private String generateSecurePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }

   
    

   
    
    
    @Override
    public Utilisateur modifierUtilisateur(Long id, Utilisateur utilisateurDetails) {
        return utilisateurData.findById(id).map(utilisateur -> {
            utilisateur.setFirstname(utilisateurDetails.getFirstname());
            utilisateur.setLastname(utilisateurDetails.getLastname());
            utilisateur.setEmail(utilisateurDetails.getEmail());
            utilisateur.setTel(utilisateurDetails.getTel());
            utilisateur.setRole(utilisateurDetails.getRole());
    
            if (utilisateurDetails.getPassword() != null && !utilisateurDetails.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(utilisateurDetails.getPassword());
                utilisateur.setPassword(encodedPassword);
            }
    
            return utilisateurData.save(utilisateur);
        }).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé !"));
    }
    
    

    @Override
    public void archiverUtilisateur(Long id) {
        utilisateurData.findById(id).ifPresent(utilisateur -> {
            utilisateur.setStatutEmploi(false);
            utilisateurData.save(utilisateur);
        });
    }

    
    @Override
    public Optional<Utilisateur> findById(Long id) {
        return utilisateurData.findById(id);
    }

    @Override
    public List<Utilisateur> findAll() {
        return utilisateurData.findAllActiveUsers(); 
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurData.findByEmail(email);
    }
    

    

    @Override
    public List<Utilisateur> searchByFirstnameOrLastname(String keyword) {
        return utilisateurData.searchByFirstnameOrLastname(keyword);
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }
    @Override
    public List<Utilisateur> findByRole(RoleEnum role) {
        return utilisateurData.findByRole(role);
    }
    
   
}