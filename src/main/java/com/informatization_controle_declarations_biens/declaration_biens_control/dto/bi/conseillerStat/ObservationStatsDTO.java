package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.conseillerStat;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObservationStatsDTO {
    private Long declarationId;
    private Long conseillerId;
    private long nombreObservationsExistantes;
    private String assujettiNom;
    private String assujettiPrenom;
    private EtatDeclarationEnum etatDeclaration;
    private boolean peutAjouterObservations;
    private LocalDate derniereObservationDate;
}
