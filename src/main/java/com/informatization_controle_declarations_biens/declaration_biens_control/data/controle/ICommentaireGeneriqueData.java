package com.informatization_controle_declarations_biens.declaration_biens_control.data.controle;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.CommentaireGenerique;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.TypeEntiteEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICommentaireGeneriqueData extends JpaRepository<CommentaireGenerique, Long> {
    List<CommentaireGenerique> findByDeclarationIdAndTypeEntite(Long declarationId, TypeEntiteEnum typeEntite);
    List<CommentaireGenerique> findByUtilisateurIdAndDeclarationIdAndTypeEntite(Long utilisateurId, Long declarationId, TypeEntiteEnum typeEntite);

}
