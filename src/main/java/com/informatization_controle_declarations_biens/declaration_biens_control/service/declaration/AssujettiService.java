package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAssujettiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.MagicTokenRepository;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatAssujettiEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.MagicToken;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAssujettiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AssujettiProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.EmailService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AssujettiService implements IAssujettiService {

    @Autowired
    private IAssujettiData assujettiData;
    @Autowired
    private IUtilisateurService utilisateurService;
@Autowired
    private MagicTokenRepository magicTokenRepository;
    
    @Autowired
    private EmailService emailService;
   
    public Assujetti save(Assujetti assujetti) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur admin = utilisateurService.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        assujetti.setAdministrateur(admin);
        assujetti.setDatePriseDeService(new Date());
        assujetti.setEtat(EtatAssujettiEnum.NOUVEAU);

         Assujetti savedAssujetti = assujettiData.save(assujetti);
        
        // Génération du token
        MagicToken token = new MagicToken();
        token.setToken(UUID.randomUUID().toString());
        token.setAssujetti(savedAssujetti);
        token.setExpiration(LocalDateTime.now().plusHours(24));
        magicTokenRepository.save(token);
        
        // Envoi de l'email
        String magicLink = "http://localhost:8084/api/declaration/access?token=" + token.getToken();
        emailService.sendEmail(
            savedAssujetti.getEmail(),
            "Accès à votre déclaration",
            "Cliquez sur ce lien pour accéder : " + magicLink
        );
        
        return savedAssujetti;
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

/*     @Override
    public List<Assujetti> findByFonction(String fonctionLibelle) {
        return assujettiData.findByFonction(fonctionLibelle);
    }
    
    @Override
    public List<Assujetti> findByInstitution(String institutionLibelle) {
        return assujettiData.findByInstitution(institutionLibelle);
    } */
    

    @Override
    public List<AssujettiProjection> getAssujettiDetails(Long id) {
        return assujettiData.getAssujettiDetails(id);
    }

    @Override
    public List<Assujetti> findByDatePriseDeServiceBetween(java.sql.Date startDate, java.sql.Date endDate) {
        return assujettiData.findByDatePriseDeServiceBetween(startDate, endDate);
    }
}
