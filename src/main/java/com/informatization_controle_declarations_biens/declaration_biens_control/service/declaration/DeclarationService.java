package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.HistoriqueDeclarationUserData;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.INotificationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.HistoriqueDeclarationUserData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.HistoriqueDeclarationUser;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDeclarationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.control.NotificationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.UtilisateurServiceImpl;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeclarationService implements IDeclarationService {

    private final UtilisateurServiceImpl utilisateurServiceImpl;
    private final NotificationService notificationService;
    private final IDeclarationData declarationData;
    private final AnimauxService animauxService;
    private final AppareilsElectroMenagersService appareilElectroMenagerService;
    private final AutresBiensDeValeurService autresBiensDeValeurService;
    private final AutresDettesService autresDettesService;
    private final DisponibilitesEnBanqueService disponibilitesEnBanqueService;
    private final EmpruntsService empruntsService;
    private final EspecesService especesService;
    private final FoncierBatiService foncierBatiService;
    private final FoncierNonBatiService foncierNonBatiService;
    private final LesCreancesService lesCreancesService;
    private final MeublesMeublantsService meublesMeublantsService;
    private final RevenusService revenusService;
    private final TitresService titresService;
    private final VehiculeService vehiculeService;
    private final HistoriqueDeclarationUserData historiqueRepository;



    
    public DeclarationService(IDeclarationData declarationData,
                             AnimauxService animauxService,
                             AppareilsElectroMenagersService appareilElectroMenagerService,
                             AutresBiensDeValeurService autresBiensDeValeurService,
                             AutresDettesService autresDettesService,
                             DisponibilitesEnBanqueService disponibilitesEnBanqueService,
                             EmpruntsService empruntsService,
                             EspecesService especesService,
                             FoncierBatiService foncierBatiService,
                             FoncierNonBatiService foncierNonBatiService,
                             LesCreancesService lesCreancesService,
                             MeublesMeublantsService meublesMeublantsService,
                             RevenusService revenusService,
                             TitresService titresService,
                             NotificationService notificationService,
                             VehiculeService vehiculeService, UtilisateurServiceImpl utilisateurServiceImpl, HistoriqueDeclarationUserData historiqueRepository) {
        this.declarationData = declarationData;
        this.animauxService = animauxService;
        this.appareilElectroMenagerService = appareilElectroMenagerService;
        this.autresBiensDeValeurService = autresBiensDeValeurService;
        this.autresDettesService = autresDettesService;
        this.disponibilitesEnBanqueService = disponibilitesEnBanqueService;
        this.empruntsService = empruntsService;
        this.especesService = especesService;
        this.foncierBatiService = foncierBatiService;
        this.foncierNonBatiService = foncierNonBatiService;
        this.lesCreancesService = lesCreancesService;
        this.meublesMeublantsService = meublesMeublantsService;
        this.revenusService = revenusService;
        this.titresService = titresService;
        this.notificationService=notificationService;
        this.vehiculeService = vehiculeService;
        this.utilisateurServiceImpl = utilisateurServiceImpl;
        this.historiqueRepository = historiqueRepository;
        
    }
        
    public DeclarationDto getFullDeclarationDetails(Long declarationId) {
        DeclarationDto dto = new DeclarationDto();
        
        Declaration declaration = declarationData.findById(declarationId).orElse(null);
        if (declaration == null) return null;
        
        dto = new DeclarationDto(declaration);
        
        dto.setAnimaux(animauxService.getAnimauxByDeclaration(declarationId));
        System.out.println(dto.getAnimaux());

        dto.setVehicules(vehiculeService.getByDeclaration(declarationId));
        dto.setFonciersBatis(foncierBatiService.getByDeclaration(declarationId));
        dto.setFonciersNonBatis(foncierNonBatiService.getByDeclaration(declarationId));
        dto.setAppareilsElectromenagers(appareilElectroMenagerService.getByDeclaration(declarationId));
        dto.setAutresBiensDeValeur(autresBiensDeValeurService.getByDeclaration(declarationId));
        dto.setDisponibilitesBanque(disponibilitesEnBanqueService.getByDeclaration(declarationId));
        dto.setEmprunts(empruntsService.getByDeclaration(declarationId));
        dto.setEspeces(especesService.getByDeclaration(declarationId));
        dto.setCreances(lesCreancesService.getByDeclaration(declarationId));
        dto.setMeublesMeublants(meublesMeublantsService.getByDeclaration(declarationId));
        dto.setRevenus(revenusService.getByDeclaration(declarationId));
        dto.setTitres(titresService.getByDeclaration(declarationId));
        dto.setAutresDettes(autresDettesService.getByDeclaration(declarationId));
        
        return dto;
    }


    @Override
    public Optional<Declaration> findById(Long id) {
        return declarationData.findById(id);
    }
    
    @Override
    public List<Declaration> findAll() {
        return declarationData.findAll();
    }
    
    @Override
    public Declaration save(Declaration entity) {
        return declarationData.save(entity);
    }
    
    @Override
    public void deleteById(Long id) {
        declarationData.deleteById(id);
    }
    @Override
public Declaration validateDeclaration(Long id) {
    Declaration declaration = declarationData.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Déclaration non trouvée"));

    
    return declarationData.save(declaration);
}

@Override
public Declaration refuseDeclaration(Long id) {
    Declaration declaration = declarationData.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Déclaration non trouvée"));
    
    
    return declarationData.save(declaration);
}

/* public Declaration assignUserToDeclaration(Long declarationId, Long utilisateurId) {
    // Récupérer la déclaration
    Declaration declaration = declarationData.findById(declarationId)
        .orElseThrow(() -> new EntityNotFoundException("Déclaration non trouvée"));
    
    // Récupérer l'utilisateur (assure-toi que utilisateurService existe)
    Utilisateur utilisateur = utilisateurServiceImpl.findById(utilisateurId)
        .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

    // Affecter l'utilisateur à la déclaration
    declaration.setUtilisateur(utilisateur);

    // Sauvegarder la déclaration mise à jour
    return declarationData.save(declaration);
} */

@Override
public Declaration assignUserToDeclaration(Long declarationId, Long utilisateurId) {
    // Récupérer la déclaration
    Declaration declaration = declarationData.findById(declarationId)
        .orElseThrow(() -> new EntityNotFoundException("Déclaration non trouvée"));

    // Récupérer l'utilisateur
    Utilisateur utilisateur = utilisateurServiceImpl.findById(utilisateurId)
        .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

    // 1. Vérifier si la déclaration a déjà un utilisateur affecté
    if (declaration.getUtilisateur() != null) {
        // 2. Trouver l'historique actif et le clôturer
        List<HistoriqueDeclarationUser> historiquesActifs = historiqueRepository
            .findByDeclarationIdAndDateFinAffectationIsNull(declarationId);

        if (!historiquesActifs.isEmpty()) {
            HistoriqueDeclarationUser historiqueActif = historiquesActifs.get(0);
            historiqueActif.setDateFinAffectation(LocalDate.now());
            historiqueRepository.save(historiqueActif);
        }
    }

    // 3. Affecter le nouvel utilisateur à la déclaration
    declaration.setUtilisateur(utilisateur);
    Declaration savedDeclaration = declarationData.save(declaration);

    // 4. Créer un nouvel historique
    HistoriqueDeclarationUser nouvelHistorique = new HistoriqueDeclarationUser();
    nouvelHistorique.setDeclaration(declaration);
    nouvelHistorique.setUtilisateur(utilisateur);
    nouvelHistorique.setDateAffectation(LocalDate.now());
    historiqueRepository.save(nouvelHistorique);

    // Création d'une notification
    String message = "Une déclaration vous a été affectée (N° de déclaration: " + declarationId + ").";
    String type = "ASSIGNMENT";
    notificationService.createAndSendNotification(utilisateurId, message, type, declarationId);

    return savedDeclaration;
} 

/* public Declaration assignUserToDeclaration(Long declarationId, Long utilisateurId) {
    // Récupérer la déclaration
    Declaration declaration = declarationData.findById(declarationId)
        .orElseThrow(() -> new EntityNotFoundException("Déclaration non trouvée"));

    // Récupérer l'utilisateur
    Utilisateur utilisateur = utilisateurServiceImpl.findById(utilisateurId)
        .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

    // Affecter l'utilisateur à la déclaration
    declaration.setUtilisateur(utilisateur);
    Declaration savedDeclaration = declarationData.save(declaration);

    // Création d'une notification avec la déclaration liée
    String message = "Une déclaration vous a été affectée (N° de déclaration: " + declarationId + ").";
    String type = "ASSIGNMENT";

    notificationService.createAndSendNotification(utilisateurId, message, type, declarationId);

    return savedDeclaration;
} */



@Override
public List<Declaration> searchByNomOrPrenomAssujetti(String keyword) {
    return declarationData.searchByNomOrPrenomAssujetti(keyword);
}

@Override
public List<Declaration> findByUtilisateur(Utilisateur utilisateur) {
    return declarationData.findByUtilisateur(utilisateur);
}

public List<Declaration> findByUtilisateurId(Long utilisateurId) {
    return declarationData.findByUtilisateurId(utilisateurId);
}

public List<Declaration> getDeclarationsByUtilisateurId(Long utilisateurId) {
    return declarationData.findByUtilisateurId(utilisateurId);
}

@Override
public boolean existsByUtilisateurId(Long utilisateurId) {
    return declarationData.existsByUtilisateurId(utilisateurId);
}



@Override
public boolean existsByAssujettiIdAndEtatDeclaration(Long assujettiId, EtatDeclarationEnum etatDeclaration) {
    return declarationData.existsByAssujettiIdAndEtatDeclaration(assujettiId, etatDeclaration);
}
// Dans DeclarationService.java
@Override
public List<Declaration> searchByUserAndKeyword(Long userId, String keyword) {
    // Solution 1: Utilisation de la méthode existante avec filtrage supplémentaire
    List<Declaration> declarations = declarationData.searchByNomOrPrenomAssujetti(keyword);
    return declarations.stream()
            .filter(d -> d.getUtilisateur() != null && d.getUtilisateur().getId().equals(userId))
            .collect(Collectors.toList());
    
    // Solution 2: Implémentation directe dans votre IDeclarationData
    // return declarationData.searchByUserAndKeyword(userId, keyword);
}



}