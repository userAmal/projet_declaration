package com.informatization_controle_declarations_biens.declaration_biens_control.dto.controle;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.CommentaireGenerique;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.TypeEntiteEnum;
import lombok.Data;

@Data
public class CommentaireGeneriqueDto {
    private Long id;
    private String commentaire;
    private TypeEntiteEnum typeEntite;
    private Long utilisateurId;
    private Long declarationId;
    private LocalDate dateComment;

    public CommentaireGeneriqueDto() {}

    public CommentaireGeneriqueDto(CommentaireGenerique commentaireGenerique) {
        this.id = commentaireGenerique.getId();
        this.commentaire = commentaireGenerique.getCommentaire();
        this.typeEntite = commentaireGenerique.getTypeEntite();
        this.utilisateurId = commentaireGenerique.getUtilisateur() != null ? commentaireGenerique.getUtilisateur().getId() : null;
        this.declarationId = commentaireGenerique.getDeclaration() != null ? commentaireGenerique.getDeclaration().getId() : null;
        this.dateComment = commentaireGenerique.getDateComment();
    }
}

