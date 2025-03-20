package com.informatization_controle_declarations_biens.declaration_biens_control.service.securite;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.scheduling.annotation.Scheduled;
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

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        log.info("Generating token for user: {}", userDetails.getUsername());
        log.info("User role: {}", userDetails.getAuthorities());
        
        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) 
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    

    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(role -> role.replace("ROLE_", "")) 
            .collect(Collectors.toList());

        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .claim("roles", roles)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }



    public boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
private Set<String> blacklistedTokens = new HashSet<>();
private Map<String, Date> tokenExpirations = new HashMap<>();

public void invalidateToken(String token) {
    blacklistedTokens.add(token);
    Date expiration = extractExpiration(token);
    tokenExpirations.put(token, expiration);
}

public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) 
            && !isTokenExpired(token)
            && !isTokenBlacklisted(token));  
}

public boolean isTokenBlacklisted(String token) {
    return blacklistedTokens.contains(token);
}

@Scheduled(fixedRate = 3600000)  
public void cleanupBlacklist() {
    Date now = new Date();
    Iterator<Map.Entry<String, Date>> iterator = tokenExpirations.entrySet().iterator();
    
    while (iterator.hasNext()) {
        Map.Entry<String, Date> entry = iterator.next();
        if (entry.getValue().before(now)) {
            blacklistedTokens.remove(entry.getKey());
            iterator.remove();
        }
    }
}
}
