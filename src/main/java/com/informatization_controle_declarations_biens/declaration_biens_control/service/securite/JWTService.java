package com.informatization_controle_declarations_biens.declaration_biens_control.service.securite;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.SignatureAlgorithm;

@Slf4j
@Service
public class JWTService {

    private static final String SECRET_KEY = "VGhpcyBpcyBhIHNlY3JldCBzaWduaW5nIGtleSBnZW5lcmF0ZWQgdXNpbmcgQmFzZTY0";

    // Extraire le nom d'utilisateur du token
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    // Extraire toutes les revendications du token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // Récupérer la clé secrète de signature
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Méthode générique pour extraire des données spécifiques du token
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    // Générer un token JWT avec des informations supplémentaires (par exemple, des rôles)
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        log.info("Generating token for user: {}", userDetails.getUsername());
        // Log the role
        log.info("User role: {}", userDetails.getAuthorities());
        
        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // 24 hours
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    

    // Générer un token JWT avec les rôles extraits de UserDetails sans le préfixe ROLE_
    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(role -> role.replace("ROLE_", "")) // Retirer le préfixe ROLE_
            .collect(Collectors.toList());

        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .claim("roles", roles)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 jour d'expiration
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    // Vérifier si le token est valide en fonction du nom d'utilisateur et de son expiration
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Vérifier si le token a expiré
    public boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    // Extraire la date d'expiration du token
    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
    
}
