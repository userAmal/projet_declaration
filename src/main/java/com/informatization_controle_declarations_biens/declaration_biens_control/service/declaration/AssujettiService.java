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
    
    @Autowired
    private DeclarationAssignmentService assignmentService;
    
    @Autowired
    private UtilisateurServiceImpl utilisateurService;

    private static final int RAPPEL_JOURS = 15;
    private static final int EXPIRATION_JOURS = 30;
    private static final int DECLARATION_ANNUELLE_JOURS = 365;
    private static final BigDecimal MONTANT_AMENDE = new BigDecimal("500.00");
    
    // Délais de production
    private static final Duration PRODUCTION_DELAY_ANNUAL = Duration.ofDays(DECLARATION_ANNUELLE_JOURS);
    private static final Duration PRODUCTION_DELAY_RAPPEL = Duration.ofDays(RAPPEL_JOURS);
    private static final Duration PRODUCTION_DELAY_EXPIRATION = Duration.ofDays(EXPIRATION_JOURS);

    // Set pour stocker les tokens expirés/invalidés
    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Constructeur
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

    private Utilisateur getDefaultUser() {
        return utilisateurService.findById(1L)
            .orElseThrow(() -> new RuntimeException("Default user not found"));
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

    /**
     * Planifie l'envoi automatique d'une déclaration de mise à jour annuelle
     */
    private void scheduleAnnualDeclaration(Assujetti assujetti, Duration delay) {
        LocalDateTime scheduledDate = LocalDateTime.now().plus(delay);
        
        log.info("Planification de la déclaration annuelle pour l'assujetti ID {} à {}", 
                assujetti.getId(), scheduledDate);
        
        taskScheduler.schedule(() -> {
            try {
                // Vérifier si l'assujetti existe toujours et n'est pas archivé
                Optional<Assujetti> currentAssujettiOpt = assujettiData.findById(assujetti.getId());
                
                if (currentAssujettiOpt.isEmpty()) {
                    log.warn("Assujetti ID {} non trouvé pour la déclaration annuelle", assujetti.getId());
                    return;
                }
                
                Assujetti currentAssujetti = currentAssujettiOpt.get();
                
                // Vérifier si l'assujetti est archivé (STOP)
                if (currentAssujetti.getEtat() == EtatAssujettiEnum.STOP) {
                    log.info("Assujetti ID {} est archivé, aucune déclaration annuelle envoyée", 
                            currentAssujetti.getId());
                    return;
                }
                
                // Créer une nouvelle déclaration de mise à jour
                Declaration nouvelleDeclaration = new Declaration();
                nouvelleDeclaration.setAssujetti(currentAssujetti);
                nouvelleDeclaration.setDateDeclaration(LocalDate.now());
                nouvelleDeclaration.setEtatDeclaration(EtatDeclarationEnum.nouveau);
                nouvelleDeclaration.setTypeDeclaration(TypeDeclarationEnum.Mise_à_jour);
                nouvelleDeclaration.setUtilisateur(getDefaultUser());
                
                // Sauvegarder la déclaration
                Declaration savedDeclaration = declarationService.save(nouvelleDeclaration);
                
                // Générer le token avec expiration après 30 jours
                String token = generateJwtToken(savedDeclaration.getId());
                String magicLink = "http://localhost:4200/#/declaration?token=" + token;
                
                // Envoyer l'email de déclaration annuelle
                Map<String, Object> annualVariables = Map.of(
                    "header", "mise_a_jour",
                    "body", "des biens et avoirs - Déclaration annuelle de mise à jour",
                    "url", magicLink
                );
                
                emailService.sendEmail(
                    currentAssujetti.getEmail(),
                    "Déclaration annuelle de mise à jour des biens",
                    "mail_pdf_Declaration",
                    annualVariables
                );
                
                log.info("Déclaration annuelle envoyée pour l'assujetti ID {} (Déclaration ID {})", 
                        currentAssujetti.getId(), savedDeclaration.getId());
                
                // Planifier le rappel après 15 jours
                scheduleRappel(savedDeclaration, currentAssujetti, magicLink, PRODUCTION_DELAY_RAPPEL);
                
                // Planifier la vérification d'expiration après 30 jours
                scheduleExpirationVerification(savedDeclaration, currentAssujetti, PRODUCTION_DELAY_EXPIRATION);
                
                // Planifier la prochaine déclaration annuelle (365 jours après celle-ci)
                scheduleAnnualDeclaration(currentAssujetti, PRODUCTION_DELAY_ANNUAL);
                
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de la déclaration annuelle pour l'assujetti ID {}: {}", 
                         assujetti.getId(), e.getMessage(), e);
            }
        }, scheduledDate.atZone(ZoneId.systemDefault()).toInstant());
    }
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
            
            // Assignment automatique au PG le moins chargé
            try {
                assignmentService.assignDeclarationToPG(
                    currentDeclaration.getId(), 
                    "Déclaration expirée - traitement de l'amende requis"
                );
            } catch (Exception e) {
                log.error("Erreur lors de l'assignment automatique pour déclaration expirée {}: {}", 
                         currentDeclaration.getId(), e.getMessage());
            }
            
            // SUPPRIMER L'ENVOI D'EMAIL D'AMENDE
            log.info("Amende générée pour déclaration ID {} (Assujetti ID {}), aucun email envoyé", 
                    currentDeclaration.getId(), assujetti.getId());
        }
    }, verificationDate.atZone(ZoneId.systemDefault()).toInstant());
}
 @Override
    public Assujetti save(Assujetti assujetti) {
        try {
            // Simple sauvegarde sans logique d'email
            return assujettiData.save(assujetti);
        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde de l'assujetti: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde: " + e.getMessage(), e);
        }
    }

    // Nouvelle méthode spécifique pour la création initiale
    @Override
    public Assujetti createInitialAssujetti(Assujetti assujetti) {
        try {
            // Enregistrer l'assujetti
            Assujetti savedAssujetti = assujettiData.save(assujetti);
            
            // Créer une nouvelle déclaration initiale liée à cet assujetti
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
            
            // Planifier le rappel après 15 jours
            scheduleRappel(savedDeclaration, savedAssujetti, magicLink, PRODUCTION_DELAY_RAPPEL);
            
            // Planifier la vérification d'expiration après 30 jours
            scheduleExpirationVerification(savedDeclaration, savedAssujetti, PRODUCTION_DELAY_EXPIRATION);
            
            // Planifier la première déclaration annuelle après 365 jours
            scheduleAnnualDeclaration(savedAssujetti, PRODUCTION_DELAY_ANNUAL);
            
            log.info("Nouvel assujetti créé avec planification des déclarations annuelles: ID {}", 
                    savedAssujetti.getId());
            
            return savedAssujetti;
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'assujetti: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création: " + e.getMessage(), e);
        }
    }
    private void scheduleRappel(Declaration declaration, Assujetti assujetti, String magicLink, Duration delay) {
        LocalDateTime rappelDate = LocalDateTime.now().plus(delay);
        
        taskScheduler.schedule(() -> {
            Declaration currentDeclaration = declarationService.findById(declaration.getId())
                .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));
                
            if (currentDeclaration.getEtatDeclaration() == EtatDeclarationEnum.nouveau) {
                String bodyMessage = currentDeclaration.getTypeDeclaration() == TypeDeclarationEnum.Initiale 
                    ? "des biens et avoirs - Il vous reste " + (EXPIRATION_JOURS - RAPPEL_JOURS) + " jours"
                    : "des biens et avoirs - Mise à jour annuelle - Il vous reste " + (EXPIRATION_JOURS - RAPPEL_JOURS) + " jours";
                    
                Map<String, Object> rappelVariables = Map.of(
                    "header", "rappel",
                    "body", bodyMessage,
                    "url", magicLink
                );
                
                String subject = currentDeclaration.getTypeDeclaration() == TypeDeclarationEnum.Initiale 
                    ? "Rappel : Déclaration initiale des biens à compléter"
                    : "Rappel : Déclaration annuelle de mise à jour à compléter";
                
                emailService.sendEmail(
                    assujetti.getEmail(),
                    subject,
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
            
            // Reprendre les déclarations annuelles après restoration
            scheduleAnnualDeclaration(assujetti, PRODUCTION_DELAY_ANNUAL);
        } else {
            throw new IllegalArgumentException("Assujetti non trouvé avec l'ID: " + id);
        }
    }

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
            log.info("Assujetti ID {} archivé - Les déclarations annuelles futures seront annulées", id);
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