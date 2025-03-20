package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAssujettiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAssujettiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AssujettiProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.EmailService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.crypto.SecretKey;

@Service
@Transactional
public class AssujettiService implements IAssujettiService {

    @Autowired
    private IAssujettiData assujettiData;
    
    @Autowired
    private DeclarationService declarationService;

    @Autowired
    private EmailService emailService;
    
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    private String generateJwtToken(Long declarationId) {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(30);
        Date expirationDate = Date.from(expiryDate.atZone(ZoneId.systemDefault()).toInstant());
        
        return Jwts.builder()
                .setSubject(declarationId.toString())
                .claim("declarationId", declarationId)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public Assujetti save(Assujetti assujetti) {
        // 1. First save the assujetti to get an ID
        Assujetti savedAssujetti = assujettiData.save(assujetti);
        
        // 2. Create a new declaration linked to this assujetti
        Declaration declaration = new Declaration();
        declaration.setAssujetti(savedAssujetti);
        declaration.setDateDeclaration(LocalDate.now());
        declaration.setEtatDeclaration(EtatDeclarationEnum.valider); // Using enum instead of int
        declaration.setTypeDeclaration(TypeDeclarationEnum.Initiale); // Using enum instead of int
        
        // 3. Save the declaration to get its ID
        Declaration savedDeclaration = declarationService.save(declaration);
        
        // 4. Generate JWT token with the declaration ID
        String token = generateJwtToken(savedDeclaration.getId());
        
        // 5. Prepare email variables
        Map<String, Object> variables = Map.of(
            "nom", savedAssujetti.getNom(),
            "prenom", savedAssujetti.getPrenom(),
            "magicLink", "http://localhost:4200/#/declaration?token=" + token
        );
        
        // 6. Send the email with the magic link
        try {
            emailService.sendEmail(savedAssujetti.getEmail(), 
                                  "Accès à votre déclaration", 
                                  "magic_link", 
                                  variables);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage(), e);
        }
        
        return savedAssujetti;
    }

    public Map<String, Object> verifyAndExtractTokenDetails(String token) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Récupérer declarationId depuis la claim personnalisée
            Long declarationId = claims.get("declarationId", Long.class);
            
            return Map.of(
                "declarationId", declarationId,
                "expiration", claims.getExpiration()
            );
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Verify and extract declaration ID from a token
     * @param token JWT token to verify
     * @return Declaration ID if valid, or null if invalid/expired
     */
    public Long verifyToken(String token) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Assujetti> findAll() {
        return assujettiData.findAll();
    }
    
    @Override
    public Optional<Assujetti> findById(Long id) {
        return assujettiData.findById(id);
    }
    
    @Override
    public void deleteById(Long id) {
        assujettiData.deleteById(id);
    }
    
    @Override
    public List<Assujetti> findByCode(String code) {
        return assujettiData.findByCode(code);
    }
    
    @Override
    public List<Assujetti> findByNom(String nom) {
        return assujettiData.findByNom(nom);
    }
    
    @Override
    public List<Assujetti> findByEmail(String email) {
        return assujettiData.findByEmail(email);
    }

    @Override
    public List<AssujettiProjection> getAssujettiDetails(Long id) {
        return assujettiData.getAssujettiDetails(id);
    }
    
    @Override
    public List<Assujetti> findByDatePriseDeServiceBetween(java.sql.Date startDate, java.sql.Date endDate) {
        return assujettiData.findByDatePriseDeServiceBetween(startDate, endDate);
    }
}