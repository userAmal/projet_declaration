package com.informatization_controle_declarations_biens.declaration_biens_control.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;
import org.springframework.util.AntPathMatcher;

import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // List of public endpoints that should not generate warnings for missing tokens
    private final List<String> publicEndpoints = Arrays.asList(
            "/api/auth/**", 
            "/api/declarations/**", 
            "/api/foncier-bati/**",
            "/api/foncier-non-bati/**",
            "/api/meubles-meublants/**",
            "/api/appareils-electromenagers/**",
            "/api/animaux/**",
            "/api/especes/**",
            "/api/emprunts/**",
            "/api/titres/**",
            "/api/les-creances/**",
            "/api/revenus/**",
            "/api/vehicules/**",
            "/api/autres-biens-de-valeur/**",
            "/api/autres-dettes/**",
            "/api/disponibilites-en-banque/**",
            "/api/type-vocabulaire/**",
            "/api/vocabulaire/**",
            "/api/assujetti/declaration/access",
            "/api/utilisateurs/**"
    );
    
    private boolean isPublicEndpoint(String requestURI) {
        return publicEndpoints.stream()
                .anyMatch(pattern -> {
                    // Handle patterns with wildcards
                    if (pattern.endsWith("/**")) {
                        String basePath = pattern.substring(0, pattern.length() - 3);
                        return requestURI.startsWith(basePath);
                    }
                    return pathMatcher.match(pattern, requestURI);
                });
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String requestURI = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");
        
        // Check if this is a public endpoint
        boolean isPublic = isPublicEndpoint(requestURI);
        
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            // Only log warning if this is not a public endpoint
            if (!isPublic) {
                log.warn("No token provided or invalid token format in Authorization header for path: {}", requestURI);
            }
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authentication successful for user: {}", userEmail);
                }
            }
        } catch (Exception e) {
            log.error("Authentication error", e);
            
            // Only send error for protected endpoints
            if (!isPublic) {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Authentication failed");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}