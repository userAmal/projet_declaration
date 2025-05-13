package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAssujettiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatAssujettiEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAssujettiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AssujettiProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.EmailService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.UtilisateurServiceImpl;

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

    @Autowired
    private IDeclarationData declarationData;

    @Autowired
private UtilisateurServiceImpl utilisateurService; // or UtilisateurData utilisateurData
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

private Utilisateur getDefaultUser() {
    // Option 1: Get a specific user by ID (most common approach)
    return utilisateurService.findById(1L) // Use the ID of your admin/system user
        .orElseThrow(() -> new RuntimeException("Default user not found"));
    
    // Option 2: Get the first available user (less ideal but works for testing)
    // return utilisateurService.findAll().stream().findFirst()
    //    .orElseThrow(() -> new RuntimeException("No users found in the system"));
}

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
        try {
            // Enregistrer l'assujetti
            Assujetti savedAssujetti = assujettiData.save(assujetti);
            
            // Créer une nouvelle déclaration liée à cet assujetti
            Declaration declaration = new Declaration();
            declaration.setAssujetti(savedAssujetti);
            declaration.setDateDeclaration(LocalDate.now());
            declaration.setEtatDeclaration(EtatDeclarationEnum.valider);
            declaration.setTypeDeclaration(TypeDeclarationEnum.Initiale);
            declaration.setUtilisateur(getDefaultUser());
            
            // Sauvegarder la déclaration
            Declaration savedDeclaration = declarationService.save(declaration);
            
            // Générer un token JWT avec l'ID de la déclaration
            String token = generateJwtToken(savedDeclaration.getId());
            
            // Préparer les variables pour l'email
            Map<String, Object> variables = Map.of(
                "nom", savedAssujetti.getNom(),
                "prenom", savedAssujetti.getPrenom(),
                "magicLink", "http://localhost:4200/#/declaration?token=" + token
            );
            
            // Envoyer l'email avec le lien magique
            try {
                emailService.sendEmail(savedAssujetti.getEmail(), 
                                      "Accès à votre déclaration", 
                                      "magic_link", 
                                      variables);
            } catch (Exception e) {
                // Gestion des erreurs d'email
                throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage(), e);
            }
            
            return savedAssujetti;
        } catch (Exception e) {
            // Gestion globale des erreurs pour l'enregistrement
            throw new RuntimeException("Erreur lors de la sauvegarde de l'assujetti et de la déclaration : " + e.getMessage(), e);
        }
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
        return assujettiData.findAssujettisExcludingEtat(EtatAssujettiEnum.STOP);
    }

    @Override
    public List<Assujetti> findAllStopped() {
        return assujettiData.findAssujettisWithEtat(EtatAssujettiEnum.STOP);
    }

    @Override
    public void restoreAssujetti(Long id) {
        Optional<Assujetti> assujettiOptional = assujettiData.findById(id);
        
        if (assujettiOptional.isPresent()) {
            Assujetti assujetti = assujettiOptional.get();
            // Changer l'état de STOP à NOUVEAU
            assujetti.setEtat(EtatAssujettiEnum.NOUVEAU);
            assujettiData.save(assujetti);
        } else {
            throw new IllegalArgumentException("Assujetti non trouvé avec l'ID: " + id);
        }
    }

/*     @Override
    public void archiverAssujetti(Long id) {
        assujettiData.findById(id).ifPresent(assujetti -> {
            assujetti.setEtat(EtatAssujettiEnum.STOP);
            assujettiData.save(assujetti);
        });
    } */

    @Override
    public void archiverAssujetti(Long id) {
        boolean hasDeclarationsEnCours = declarationData.existsByAssujettiIdAndEtatDeclaration(id, EtatDeclarationEnum.en_cours);
        
        if (hasDeclarationsEnCours) {
            throw new IllegalStateException("Impossible d'archiver un assujetti lié à une  déclaration en cours.");
        }
    
        assujettiData.findById(id).ifPresent(assujetti -> {
            assujetti.setEtat(EtatAssujettiEnum.STOP);
            assujettiData.save(assujetti);
        });
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