package com.informatization_controle_declarations_biens.declaration_biens_control.controller.securite;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurController.class);

    @Autowired
    private IUtilisateurService utilisateurService;

    @PostMapping
    public ResponseEntity<Utilisateur> ajouterUtilisateur(@RequestBody Utilisateur utilisateur) {
        try {
            Utilisateur savedUser = utilisateurService.save(utilisateur);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); 
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> modifierUtilisateur(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
        try {
            Utilisateur updatedUser = utilisateurService.modifierUtilisateur(id, utilisateur);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Erreur lors de la modification de l'utilisateur", e);
            return ResponseEntity.status(500).body(null);  
        }
    }
    
    

    @PutMapping("/{id}/archiver")
    public ResponseEntity<Void> archiverUtilisateur(@PathVariable Long id) {
        try {
            utilisateurService.archiverUtilisateur(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUserById(@PathVariable Long id) {
        try {
            return utilisateurService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        try {
            List<String> roles = Arrays.stream(RoleEnum.values())
                                       .map(Enum::name)
                                       .toList();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des rôles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Utilisateur>> getAllUsers() {
        try {
            return ResponseEntity.ok(utilisateurService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); 
        }
    }
    @GetMapping("/role/{role}")
    public ResponseEntity<List<Utilisateur>> getUsersByRole(@PathVariable String role) {
        try {
            logger.debug("Tentative de récupération des utilisateurs avec rôle : {}", role);
            
            RoleEnum roleEnum = Arrays.stream(RoleEnum.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rôle invalide"));

            List<Utilisateur> users = utilisateurService.findByRole(roleEnum);
            
            logger.info("{} utilisateurs trouvés avec le rôle {}", users.size(), role);
            return ResponseEntity.ok(users);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Rôle invalide demandé : {}", role);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erreur serveur lors de la récupération des utilisateurs", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
        @RequestBody ChangePasswordRequest request, 
        @AuthenticationPrincipal Utilisateur utilisateur
    ) {
        try {
            if (utilisateur == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
            }
    
            AuthenticationResponse response = utilisateurService.changePassword(utilisateur.getEmail(), request.getNewPassword());
    
            return ResponseEntity.ok(response); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
        }
    }
    

    @GetMapping("/search")
    public ResponseEntity<List<Utilisateur>> searchUsers(@RequestParam String keyword) {
        try {
            List<Utilisateur> users = utilisateurService.searchByFirstnameOrLastname(keyword);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/{id}/role")
public ResponseEntity<Map<String, String>> getUserRole(@PathVariable Long id) {
    try {
        return utilisateurService.findById(id)
                .map(user -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("role", user.getRole().name());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    } catch (Exception e) {
        logger.error("Erreur lors de la récupération du rôle pour l'utilisateur ID: {}", id, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
}
