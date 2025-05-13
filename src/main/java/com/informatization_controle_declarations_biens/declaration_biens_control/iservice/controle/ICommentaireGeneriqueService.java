package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle;


import java.util.List;


import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.CommentaireGenerique;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.TypeEntiteEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;

public interface ICommentaireGeneriqueService extends IGenericService<CommentaireGenerique, Long> {
    CommentaireGenerique ajouterCommentaire(Long declarationId, TypeEntiteEnum typeEntite, String commentaire, Long utilisateurId);
    List<CommentaireGenerique> getCommentairesParDeclarationEtType(Long declarationId, TypeEntiteEnum typeEntite);
    CommentaireGenerique updateCommentaire(Long id, CommentaireGenerique commentaire);
    List<CommentaireGenerique> getCommentairesParUtilisateurEtDeclarationEtType(Long utilisateurId, Long declarationId, TypeEntiteEnum typeEntite);


    

}