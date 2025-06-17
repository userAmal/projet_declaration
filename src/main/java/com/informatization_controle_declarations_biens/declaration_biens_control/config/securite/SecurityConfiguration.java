package com.informatization_controle_declarations_biens.declaration_biens_control.config.securite;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import com.informatization_controle_declarations_biens.declaration_biens_control.config.JWTAuthenticationFilter;


import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration implements WebMvcConfigurer {

    private final JWTAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

  public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get absolute path to the uploads directory
       String uploadDir = Paths.get("").toAbsolutePath().toString() + "/uploads/";
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:4201")); // Autorise les deux applications Angular
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
                config.setAllowCredentials(true);
                return config;
            }))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/uploads/**").permitAll() // Autorisation accès fichiers


.requestMatchers("/api/declarations/**").permitAll()
.requestMatchers("/api/foncier-bati/**").permitAll()
.requestMatchers("/api/foncier-non-bati/**").permitAll()
.requestMatchers("/api/meubles-meublants/**").permitAll()
.requestMatchers("/api/appareils-electromenagers/**").permitAll()
.requestMatchers("/api/animaux/**").permitAll()
.requestMatchers("/api/especes/**").permitAll()
.requestMatchers("/api/emprunts/**").permitAll()
.requestMatchers("/api/titres/**").permitAll()
.requestMatchers("/api/les-creances/**").permitAll()
.requestMatchers("/api/revenus/**").permitAll()
.requestMatchers("/api/vehicules/**").permitAll()
.requestMatchers("/api/autres-biens-de-valeur/**").permitAll()
.requestMatchers("/api/autres-dettes/**").permitAll()
.requestMatchers("/api/disponibilites-en-banque/**").permitAll()

.requestMatchers("/api/type-vocabulaire/**").permitAll()
.requestMatchers("/api/vocabulaire/**").permitAll()
.requestMatchers("/api/assujetti/declaration/access").permitAll()
.requestMatchers("/api/assujetti/**").hasAuthority("administrateur")
.requestMatchers("/api/utilisateurs/**").permitAll()
.requestMatchers("/api/parametrages**").permitAll()
.requestMatchers("/api/commentaires/**").permitAll()
.requestMatchers("/api/conclusions/**").permitAll()
            .requestMatchers("/api/avocat-general/statistics/**").permitAll()


//.requestMatchers("/api/declarations/mes-declarations").authenticated()

.anyRequest().authenticated()
                
            )
            
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
