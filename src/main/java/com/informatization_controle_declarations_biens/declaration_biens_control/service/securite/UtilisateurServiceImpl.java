package com.informatization_controle_declarations_biens.declaration_biens_control.service.securite;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.controller.securite.AuthenticationResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.securite.IUtilisateurData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UtilisateurServiceImpl implements IUtilisateurService {

    private final IUtilisateurData utilisateurData;
    private final PasswordEncoder passwordEncoder;
    private final IDeclarationData declarationData;

    @Autowired
    private JWTService jwtService;

    public UtilisateurServiceImpl(
            IUtilisateurData utilisateurData,
            IDeclarationData declarationData,
            @Lazy PasswordEncoder passwordEncoder

    ) {
        this.utilisateurData = utilisateurData;
        this.passwordEncoder = passwordEncoder;
        this.declarationData = declarationData;

    }
@Autowired
private EmailService emailService;

@Override
public Utilisateur save(Utilisateur utilisateur) {
    String generatedPassword = generateSecurePassword();
    String encodedPassword = passwordEncoder.encode(generatedPassword);
    utilisateur.setPassword(encodedPassword);

    utilisateurData.save(utilisateur);

    // Préparer les variables pour le template HTML avec un contenu enrichi
    Map<String, Object> variables = Map.of(
        "header", "Bienvenue à la Cour des comptes du Niger",
        "body", "<strong>Cher(e) " + utilisateur.getFirstname() + ",</strong><br><br>" +
                "Nous avons le plaisir de vous informer que votre compte utilisateur a été créé avec succès " +
                "dans le système de gestion de la Cour des comptes du Niger.<br><br>" +
                
                "<strong style='color: #253342; font-size: 18px;'>Détails de votre compte</strong><br><br>" +
                "<div style='background-color: #f8f9fa; border-left: 4px solid #ffa726; padding: 18px; margin: 15px auto; width: 85%; border-radius: 4px;'>" +
                "  <div style='display: table; width: 100%;'>" +
                "    <div style='display: table-row;'>" +
                "      <div style='display: table-cell; width: 150px; padding-bottom: 12px; color: #555;'>Email :</div>" +
                "      <div style='display: table-cell; font-weight: bold; color: #333;'>" + utilisateur.getEmail() + "</div>" +
                "    </div>" +
                "    <div style='display: table-row;'>" +
                "      <div style='display: table-cell; width: 150px; color: #555;'>Mot de passe :</div>" +
                "      <div style='display: table-cell; font-weight: bold;'>" +
                "        <span style='color: #333; background-color: #fff; padding: 4px 10px; border-radius: 3px; border: 1px solid #ddd;'>" + 
                            generatedPassword + 
                        "</span>" +
                "      </div>" +
                "    </div>" +
                "  </div>" +
                "</div><br>" +
                
                "Pour des raisons de sécurité, nous vous recommandons vivement de modifier votre mot de passe " +
                "dès votre première connexion.<br><br>" +
                
                "Veuillez cliquer sur le bouton ci-dessous pour accéder à la plateforme :<br>",
        "url", "http://localhost:4200/api/auth/authenticate"
    );

    try {
        // On utilise toujours le même service EmailService mais avec les nouvelles variables
        emailService.sendEmail(utilisateur.getEmail(), "Bienvenue à la Cour des comptes du Niger", "account_creation", variables);
    } catch (Exception e) {
        throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage(), e);
    }

    return utilisateur;
}
    public AuthenticationResponse changePassword(String email, String newPassword) {
        Utilisateur utilisateur = utilisateurData.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        utilisateur.setPassword(passwordEncoder.encode(newPassword));
        utilisateur.setFirstLogin(false);
        utilisateurData.save(utilisateur);
        utilisateurData.flush();

        String newJwtToken = jwtService.generateToken(utilisateur);

        return AuthenticationResponse.builder()
                .token(newJwtToken)
                .id(utilisateur.getId())
                .build();
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

   /*  @Override
    public Utilisateur modifierUtilisateur(Long id, Utilisateur utilisateurDetails) {
        Optional<Utilisateur> existingUtilisateurOpt = utilisateurData.findById(id);

        if (existingUtilisateurOpt.isPresent()) {
            Utilisateur existingUtilisateur = existingUtilisateurOpt.get();

            if (utilisateurDetails.getFirstname() != null) {
                existingUtilisateur.setFirstname(utilisateurDetails.getFirstname());
            }
            if (utilisateurDetails.getLastname() != null) {
                existingUtilisateur.setLastname(utilisateurDetails.getLastname());
            }
            if (utilisateurDetails.getEmail() != null) {
                existingUtilisateur.setEmail(utilisateurDetails.getEmail());
            }
            if (utilisateurDetails.getTel() != null) {
                existingUtilisateur.setTel(utilisateurDetails.getTel());
            }
            if (utilisateurDetails.getRole() != null) {
                existingUtilisateur.setRole(utilisateurDetails.getRole());
            }

            if (utilisateurDetails.getPassword() != null && !utilisateurDetails.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(utilisateurDetails.getPassword());
                existingUtilisateur.setPassword(encodedPassword);
            }

            return utilisateurData.save(existingUtilisateur);
        } else {
            throw new RuntimeException("Utilisateur non trouvé");
        }
    }  */


    @Override
public Utilisateur modifierUtilisateur(Long id, Utilisateur utilisateurDetails) {
    Optional<Utilisateur> existingUtilisateurOpt = utilisateurData.findById(id);

    if (existingUtilisateurOpt.isEmpty()) {
        throw new RuntimeException("Utilisateur non trouvé");
    }
    
    Utilisateur existingUtilisateur = existingUtilisateurOpt.get();

    // Validation email
    if (utilisateurDetails.getEmail() != null) {
        if (utilisateurDetails.getEmail().isEmpty()) {
            throw new RuntimeException("L'email ne peut pas être vide");
        }
        if (!utilisateurDetails.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new RuntimeException("Format d'email invalide");
        }
        // Vérifier si l'email existe déjà pour un autre utilisateur
        if (!utilisateurDetails.getEmail().equals(existingUtilisateur.getEmail()) &&
            utilisateurData.existsByEmail(utilisateurDetails.getEmail())) {
            throw new RuntimeException("Cette adresse email est déjà utilisée");
        }
        existingUtilisateur.setEmail(utilisateurDetails.getEmail());
    }

    // Validation nom et prénom
    if (utilisateurDetails.getFirstname() != null) {
        if (utilisateurDetails.getFirstname().trim().isEmpty()) {
            throw new RuntimeException("Le prénom ne peut pas être vide");
        }
        existingUtilisateur.setFirstname(utilisateurDetails.getFirstname());
    }
    
    if (utilisateurDetails.getLastname() != null) {
        if (utilisateurDetails.getLastname().trim().isEmpty()) {
            throw new RuntimeException("Le nom ne peut pas être vide");
        }
        existingUtilisateur.setLastname(utilisateurDetails.getLastname());
    }
    
    // Validation téléphone
    if (utilisateurDetails.getTel() != null) {
        if (utilisateurDetails.getTel().trim().isEmpty()) {
            throw new RuntimeException("Le numéro de téléphone ne peut pas être vide");
        }
        if (!utilisateurDetails.getTel().matches("^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$")) {
            throw new RuntimeException("Format de téléphone invalide");
        }
        // Vérifier si le téléphone existe déjà pour un autre utilisateur
        if (!utilisateurDetails.getTel().equals(existingUtilisateur.getTel()) &&
            utilisateurData.existsByTel(utilisateurDetails.getTel())) {
            throw new RuntimeException("Ce numéro de téléphone est déjà utilisé");
        }
        existingUtilisateur.setTel(utilisateurDetails.getTel());
    }
    
    // Validation rôle
    if (utilisateurDetails.getRole() != null) {
        existingUtilisateur.setRole(utilisateurDetails.getRole());
    }

    // Validation mot de passe
    if (utilisateurDetails.getPassword() != null && !utilisateurDetails.getPassword().isEmpty()) {
        if (utilisateurDetails.getPassword().length() < 8) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 8 caractères");
        }
        String encodedPassword = passwordEncoder.encode(utilisateurDetails.getPassword());
        existingUtilisateur.setPassword(encodedPassword);
    }

    return utilisateurData.save(existingUtilisateur);
}

@Override
public List<Utilisateur> findAllArchived() {
    // Cette méthode retourne tous les utilisateurs archivés (statutEmploi = false)
    return utilisateurData.findAllArchivedUsers();
}

@Override
public void restoreUtilisateur(Long id) {
    Optional<Utilisateur> utilisateurOptional = utilisateurData.findById(id);
    
    if (utilisateurOptional.isPresent()) {
        Utilisateur utilisateur = utilisateurOptional.get();
        // Changer le statut d'emploi à true pour restaurer l'utilisateur
        utilisateur.setStatutEmploi(true);
        utilisateurData.save(utilisateur);
    } else {
        throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id);
    }
}


/* 
    @Override
    public void archiverUtilisateur(Long id) {
        utilisateurData.findById(id).ifPresent(utilisateur -> {
            utilisateur.setStatutEmploi(false);
            utilisateurData.save(utilisateur);
        });
    }*/
    



    @Override
    public void archiverUtilisateur(Long id) {
        boolean hasDeclarations = declarationData.existsByUtilisateurId(id);
        if (hasDeclarations) {
            throw new IllegalStateException("Impossible d'archiver un utilisateur lié à une ou plusieurs déclarations.");
        }

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