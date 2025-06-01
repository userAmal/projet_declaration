package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAssujettiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Amende;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.StatutAmendeEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatAssujettiEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAssujettiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AssujettiProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.bi.DeclarationAssignmentService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.control.AmendeService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.EmailService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.UtilisateurServiceImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;
@Slf4j
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
    private TaskScheduler taskScheduler;
    
    @Autowired
    private AmendeService amendeService;
    
    @Autowired
    private IDeclarationData declarationData;
    

    
    private static final int RAPPEL_JOURS = 15;
    private static final int EXPIRATION_JOURS = 30;
    private static final BigDecimal MONTANT_AMENDE = new BigDecimal("500.00");
    
    @Autowired
    private UtilisateurServiceImpl utilisateurService;

    public AssujettiService(IAssujettiData assujettiData, 
                          DeclarationService declarationService,
                          EmailService emailService,
                          TaskScheduler taskScheduler,
                          AmendeService amendeService) {
        this.assujettiData = assujettiData;
        this.declarationService = declarationService;
        this.emailService = emailService;
        this.taskScheduler = taskScheduler;
        this.amendeService = amendeService;
    }

    
  // Set pour stocker les tokens expirés/invalidés
    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    private Utilisateur getDefaultUser() {
        return utilisateurService.findById(1L)
            .orElseThrow(() -> new RuntimeException("Default user not found"));
    }
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
@Autowired
private DeclarationAssignmentService assignmentService;

// Modifier la méthode scheduleExpirationVerification
private void scheduleExpirationVerification(Declaration declaration, Assujetti assujetti, Duration delay) {
    LocalDateTime verificationDate = LocalDateTime.now().plus(delay);
    
    taskScheduler.schedule(() -> {
        Declaration currentDeclaration = declarationService.findById(declaration.getId())
            .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));
            
        if (currentDeclaration.getEtatDeclaration() == EtatDeclarationEnum.nouveau) {
            // Token expiré - Changer l'état à non_declare
            currentDeclaration.setEtatDeclaration(EtatDeclarationEnum.No_declaré);
            declarationService.save(currentDeclaration);
            
            // Créer une amende
            Amende amende = new Amende();
            amende.setDeclaration(currentDeclaration);
            amende.setDateAmende(LocalDate.now());
            amende.setMontant(MONTANT_AMENDE);
            amende.setStatut(StatutAmendeEnum.NonPayee);
            amende.setMotif("Déclaration non soumise dans les délais - Token expiré");
            
            amendeService.save(amende);
            
            // NOUVEAU: Assignment automatique au PG le moins chargé
            try {
                assignmentService.assignDeclarationToPG(
                    currentDeclaration.getId(), 
                    "Déclaration expirée - traitement de l'amende requis"
                );
            } catch (Exception e) {
                log.error("Erreur lors de l'assignment automatique pour déclaration expirée {}: {}", 
                         currentDeclaration.getId(), e.getMessage());
            }
            
            // Envoyer email d'amende à l'assujetti
            Map<String, Object> amendeVariables = Map.of(
                "header", "expiration",
                "body", "Votre token d'accès a expiré. Une amende de " + MONTANT_AMENDE + " FCFA a été appliquée",
                "url", "#"
            );
            
            emailService.sendEmail(
                assujetti.getEmail(),
                "Token expiré - Amende appliquée",
                "mail_pdf_Declaration",
                amendeVariables
            );
        }
    }, verificationDate.atZone(ZoneId.systemDefault()).toInstant());
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
            declaration.setEtatDeclaration(EtatDeclarationEnum.nouveau);
            declaration.setTypeDeclaration(TypeDeclarationEnum.Initiale);
            declaration.setUtilisateur(getDefaultUser());
            
            // Sauvegarder la déclaration
            Declaration savedDeclaration = declarationService.save(declaration);
            
            // Générer le token avec expiration après 30 jours
            String token = generateJwtToken(savedDeclaration.getId());
            String magicLink = "http://localhost:4200/#/declaration?token=" + token;
            
            // Envoyer l'email initial avec le template mail_pdf_Declaration.html
            Map<String, Object> initialVariables = Map.of(
                "header", "initiale",
                "body", "des biens et avoirs",
                "url", magicLink
            );
            
            emailService.sendEmail(
                savedAssujetti.getEmail(),
                "Accès à votre déclaration initiale des biens",
                "mail_pdf_Declaration",
                initialVariables
            );
            
            // Planifier le rappel après 15 jours (pour test: 1 minute)
            scheduleRappel(savedDeclaration, savedAssujetti, magicLink, Duration.ofMinutes(1));
            
            // Planifier la vérification d'expiration après 30 jours (pour test: 2 minutes)
            scheduleExpirationVerification(savedDeclaration, savedAssujetti, Duration.ofMinutes(2));
            
            return savedAssujetti;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde: " + e.getMessage(), e);
        }
    }

    private void scheduleRappel(Declaration declaration, Assujetti assujetti, String magicLink, Duration delay) {
        LocalDateTime rappelDate = LocalDateTime.now().plus(delay);
        
        taskScheduler.schedule(() -> {
            Declaration currentDeclaration = declarationService.findById(declaration.getId())
                .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));
                
            if (currentDeclaration.getEtatDeclaration() == EtatDeclarationEnum.nouveau) {
                Map<String, Object> rappelVariables = Map.of(
                    "header", "rappel",
                    "body", "des biens et avoirs - Il vous reste " + (EXPIRATION_JOURS - RAPPEL_JOURS) + " jours",
                    "url", magicLink
                );
                
                emailService.sendEmail(
                    assujetti.getEmail(),
                    "Rappel : Déclaration initiale des biens à compléter",
                    "mail_pdf_Declaration",
                    rappelVariables
                );
            }
        }, rappelDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    // Méthode pour invalider un token après enregistrement
    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }
    
    // Méthode pour vérifier si un token est invalide
    private boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }

    public Map<String, Object> verifyAndExtractTokenDetails(String token) {
        try {
            // Vérifier si le token a été invalidé
            if (isTokenInvalidated(token)) {
                return null;
            }
            
            var claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long declarationId = claims.get("declarationId", Long.class);
            
            return Map.of(
                "declarationId", declarationId,
                "expiration", claims.getExpiration()
            );
        } catch (Exception e) {
            return null;
        }
    }
    
    public Long verifyToken(String token) {
        try {
            // Vérifier si le token a été invalidé
            if (isTokenInvalidated(token)) {
                return null;
            }
            
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

/*     @Override
    public void archiverAssujetti(Long id) {
        boolean hasDeclarationsEnCours = declarationData.existsByAssujettiIdAndEtatDeclaration(id, EtatDeclarationEnum.en_cours);
        
        if (hasDeclarationsEnCours) {
            throw new IllegalStateException("Impossible d'archiver un assujetti lié à une  déclaration en cours.");
        }
    
        assujettiData.findById(id).ifPresent(assujetti -> {
            assujetti.setEtat(EtatAssujettiEnum.STOP);
            assujettiData.save(assujetti);
        });
    } */
    
    @Override
public void archiverAssujetti(Long id) {
    // Liste des états qui bloquent l'archivage
    List<EtatDeclarationEnum> etatsBloquants = Arrays.asList(
        EtatDeclarationEnum.nouveau,
        EtatDeclarationEnum.en_cours,
        EtatDeclarationEnum.traitement,
        EtatDeclarationEnum.jugement,
        EtatDeclarationEnum.No_declaré
    );
    
    // Vérifier s'il existe des déclarations dans ces états
    boolean hasDeclarationsBloquantes = declarationData.existsByAssujettiIdAndEtatDeclarationIn(id, etatsBloquants);
    
    if (hasDeclarationsBloquantes) {
        throw new IllegalStateException("Impossible d'archiver un assujetti lié à une déclaration dans un état non final (nouveau, en cours, traitement, jugement ou non déclaré).");
    }
    
    // Si aucune déclaration bloquante, procéder à l'archivage
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