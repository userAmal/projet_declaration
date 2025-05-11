package com.informatization_controle_declarations_biens.declaration_biens_control.projection.controle;

import java.time.LocalDate;

public interface CommentaireGeneriqueProjection {
    String getCommentaire();
    String getTypeEntite();
    UtilisateurProjection getUtilisateur();
    LocalDate getDateComment();
    interface UtilisateurProjection {
        String getNom();
    }
}

