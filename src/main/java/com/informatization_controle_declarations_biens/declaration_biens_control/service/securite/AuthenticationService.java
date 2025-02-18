package com.informatization_controle_declarations_biens.declaration_biens_control.service.securite;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.controller.securite.AuthenticationRequest;
import com.informatization_controle_declarations_biens.declaration_biens_control.controller.securite.AuthenticationResponse;
import com.informatization_controle_declarations_biens.declaration_biens_control.controller.securite.RegisterRequest;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final IUtilisateurService repository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;



    public AuthenticationResponse register(RegisterRequest request) {
        Utilisateur user = Utilisateur.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .statutEmploi(true) // Mets true ou false selon ton besoin
            .tel(request.getTel())
            .role(RoleEnum.administrateur) // Enum en majuscules
            .build();
    
        repository.save(user);
    
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
    

    // Méthode pour authentifier un utilisateur
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        Utilisateur user = repository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
