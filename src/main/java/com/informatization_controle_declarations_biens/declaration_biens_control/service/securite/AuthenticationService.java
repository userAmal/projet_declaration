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

    // public AuthenticationResponse register(RegisterRequest request) {
    //     log.info("Tentative d'inscription avec rôle: {}", request.getRole());
    //     // Validation basique du rôle
    //     if(request.getRole() == null) {
    //         throw new IllegalArgumentException("Le rôle est obligatoire");
    //     }

    //     // Création de l'utilisateur
    //     Utilisateur user = Utilisateur.builder()
    //         .firstname(request.getFirstname())
    //         .lastname(request.getLastname())
    //         .email(request.getEmail())
    //         .password(passwordEncoder.encode(request.getPassword()))
    //         .statutEmploi(true)
    //         .tel(request.getTel())
    //         .role(request.getRole())
    //         .build();

    //     repository.save(user);
    //     log.debug("Utilisateur créé avec succès - ID: {}, Rôle: {}", user.getId(), user.getRole());

    //     String jwtToken = jwtService.generateToken(user);
    //     return AuthenticationResponse.builder().token(jwtToken).build();
    // }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Tentative d'authentification pour: {}", request.getEmail());
    
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        Utilisateur user = repository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        log.info("Authentification réussie - Utilisateur: {}, Rôle: {}", user.getEmail(), user.getRole().name());
    
        String jwtToken = jwtService.generateToken(user);
    
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .id(user.getId())  
            .firstname(user.getFirstname())
            .lastname(user.getLastname())
            .build();
    }
    
}
