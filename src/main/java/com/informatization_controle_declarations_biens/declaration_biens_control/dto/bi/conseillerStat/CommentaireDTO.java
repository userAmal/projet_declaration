package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentaireDTO {
    private String contenu;
    private LocalDate dateCreation;
    private Long auteurId;
    private String auteurNom;
    private TypeEntiteEnum typeEntite;
}
