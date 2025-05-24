package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsPeriodeDto {
    private String periode;
    private long declarations;
    private long rapportsProvisoires;
    private long rapportsDefinitifs;
    private long acceptees;
    private long refusees;
    private long amendes;
}
