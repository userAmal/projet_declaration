package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;



import java.util.List;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;


public interface IDeclarationService extends IGenericService<Declaration, Long> {
  DeclarationDto getFullDeclarationDetails(Long declarationId);
  Declaration validateDeclaration(Long id);
  Declaration refuseDeclaration(Long id);
  Declaration assignUserToDeclaration(Long declarationId, Long utilisateurId);
  List<Declaration> searchByNomOrPrenomAssujetti(String keyword);
  List<Declaration> findByUtilisateur(Utilisateur utilisateur);
  List<Declaration> findByUtilisateurId(Long utilisateurId);
  List<Declaration> getDeclarationsByUtilisateurId(Long utilisateurId);
  boolean existsByUtilisateurId(Long utilisateurId);
  boolean existsByAssujettiIdAndEtatDeclaration(Long assujettiId, EtatDeclarationEnum etatDeclaration);
  List<Declaration> searchByUserAndKeyword(Long userId, String keyword);







}