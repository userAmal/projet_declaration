package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.HistoriqueDeclarationUserData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.HistoriqueDeclarationUser;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IHistoriqueDeclarationUserService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.UtilisateurServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoriqueDeclarationUserServiceImpl implements IHistoriqueDeclarationUserService {

    private final HistoriqueDeclarationUserData historiqueRepository;
    private final UtilisateurServiceImpl utilisateurService;
    private final DeclarationService declarationService;

    @Override
    @Transactional
    public HistoriqueDeclarationUser createHistorique(Long declarationId, Long utilisateurId) {
        Declaration declaration = declarationService.findById(declarationId)
                .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));
        Utilisateur utilisateur = utilisateurService.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        HistoriqueDeclarationUser historique = HistoriqueDeclarationUser.builder()
                .declaration(declaration)
                .utilisateur(utilisateur)
                .dateAffectation(LocalDate.now())
                .build();

        return historiqueRepository.save(historique);
    }

    @Override
    @Transactional
    public HistoriqueDeclarationUser closeAffectation(Long historiqueId) {
        HistoriqueDeclarationUser historique = historiqueRepository.findById(historiqueId)
                .orElseThrow(() -> new RuntimeException("Historique non trouvé"));
        historique.setDateFinAffectation(LocalDate.now());
        return historiqueRepository.save(historique);
    }

    @Override
    public List<HistoriqueDeclarationUser> getHistoriqueByDeclaration(Long declarationId) {
        return historiqueRepository.findByDeclarationId(declarationId);
    }

    @Override
    public List<HistoriqueDeclarationUser> getHistoriqueByUtilisateur(Long utilisateurId) {
        return historiqueRepository.findByUtilisateurId(utilisateurId);
    }

    @Override
    public List<HistoriqueDeclarationUser> getHistoriqueByRole(RoleEnum role) {
        return historiqueRepository.findByRole(role);
    }

    @Override
    public List<HistoriqueDeclarationUser> getActiveAffectations() {
        return historiqueRepository.findActiveAffectations();
    }

    @Override
    public List<HistoriqueDeclarationUser> getActiveAffectationsByDeclaration(Long declarationId) {
        return historiqueRepository.findActiveAffectationsByDeclaration(declarationId);
    }
    public Optional<Utilisateur> getFirstUtilisateurByRoleAndDeclaration(RoleEnum role, Long declarationId) {
    return historiqueRepository.findFirstUtilisateurByRoleAndDeclarationId(role, declarationId);
}

    @Override
    public boolean existsAffectation(Long declarationId, Long utilisateurId) {
        return historiqueRepository.existsByDeclarationIdAndUtilisateurId(declarationId, utilisateurId);
    }

    @Override
    public List<HistoriqueDeclarationUser> getHistoriqueByPeriod(LocalDate startDate, LocalDate endDate) {
        return historiqueRepository.findByPeriod(startDate, endDate);
    }
}