package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsByEtatDTO {
    private String etat;
    private long count;
    private double pourcentage;
}