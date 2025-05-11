package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Conclusion;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;

public interface IConclusionService {

    Conclusion genererConclusion(Declaration declaration, byte[] pdfContent, String fileName);
    Conclusion genererLettreOfficielle(Utilisateur utilisateur, Declaration declaration, String contenuUtilisateur);
    ResponseEntity<Resource> telechargerConclusion(Long conclusionId);
    List<Conclusion> getConclusionsByDeclaration(Long declarationId);
    List<Conclusion> getConclusionsByUtilisateur(Long utilisateurId);
    Optional<Conclusion> findById(Long id); 
    void deleteConclusion(Long id);
    List<Conclusion> findByUtilisateurAndDeclaration(Long utilisateurId, Long declarationId);

}