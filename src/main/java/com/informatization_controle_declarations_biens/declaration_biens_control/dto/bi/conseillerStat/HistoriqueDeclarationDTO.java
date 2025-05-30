package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import java.time.LocalDate;
import java.util.List;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoriqueDeclarationDTO {
    private Long declarationId;
    private String assujettiInfo;
    private TypeDeclarationEnum typeDeclaration;
    private LocalDate dateCreation;
    private EtatDeclarationEnum etatActuel;
    private List<AffectationDTO> historiqueAffectations;
    private List<RapportDTO> rapports;
    private List<CommentaireDTO> commentaires;
}

