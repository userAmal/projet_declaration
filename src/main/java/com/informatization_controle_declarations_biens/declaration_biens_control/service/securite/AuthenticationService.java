package com.informatization_controle_declarations_biens.declaration_biens_control.service.securite;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.controller.securite.AuthenticationRequest;
import com.informatization_controle_declarations_biens.declaration_biens_control.controller.securite.AuthenticationResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final IUtilisateurService repository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Tentative d'authentification pour: {}", request.getEmail());
        
        try {
            // Authenticate user credentials
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Find user by email
            Utilisateur user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Utilisateur non trouvé pour l'email: {}", request.getEmail());
                    return new RuntimeException("Utilisateur non trouvé");
                });
            
            // Validate user ID is not null or 0
            if (user.getId() == null || user.getId() == 0) {
                log.error("ID utilisateur invalide: {}", user.getId());
                throw new RuntimeException("ID utilisateur invalide");
            }
            
            log.info("Authentification réussie - Utilisateur: {}, ID: {}, Rôle: {}", 
                    user.getEmail(), user.getId(), user.getRole().name());
    
            String jwtToken = jwtService.generateToken(user);
    
            return AuthenticationResponse.builder()
                .token(jwtToken)
                .id(user.getId())  
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
                
        } catch (Exception e) {
            log.error("Erreur lors de l'authentification pour {}: {}", request.getEmail(), e.getMessage());
            throw new RuntimeException("Erreur d'authentification: " + e.getMessage());
        }
    }
}
