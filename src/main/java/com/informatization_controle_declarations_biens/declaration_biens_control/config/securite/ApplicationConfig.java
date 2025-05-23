package com.informatization_controle_declarations_biens.declaration_biens_control.config.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.informatization_controle_declarations_biens.declaration_biens_control.config.CustomPasswordEncoder;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor

public class ApplicationConfig {
    private final IUtilisateurService utilisateurService;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> utilisateurService.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use custom password encoder that handles both BCrypt and plain text
        return new CustomPasswordEncoder();
    }
}
