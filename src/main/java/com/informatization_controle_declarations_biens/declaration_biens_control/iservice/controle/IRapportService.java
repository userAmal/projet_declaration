package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface IRapportService {
    Rapport genererRapportProvisoire(Utilisateur utilisateur, Declaration declaration, String contenu);
Rapport genererRapportDefinitif(Utilisateur utilisateur, 
                                  Declaration declaration, 
                                  Boolean decision,
                                  String contenu);
    ResponseEntity<Resource> telechargerRapport(Long rapportId);
    List<Rapport> getRapportsByDeclaration(Long declarationId);
    List<Rapport> getRapportsByUtilisateur(Long utilisateurId);
    List<Rapport> getRapportsByType(Rapport.Type type);
    Rapport getRapportById(Long id);
    void deleteRapport(Long id);
}