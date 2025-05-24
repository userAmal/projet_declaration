package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowStatsDTO {
    private long enAttente;
    private long enTraitement;
    private long terminees;
    private double tempsTraitementMoyen;
    private long retards;
}
