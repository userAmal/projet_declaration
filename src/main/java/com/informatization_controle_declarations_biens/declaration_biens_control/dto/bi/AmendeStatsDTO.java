package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmendeStatsDTO {
    private long totalAmendes;
    private long amendesPayees;
    private long amendesNonPayees;
    private long amendesAnnulees;
    private BigDecimal montantTotal;
    private BigDecimal montantPaye;
    private BigDecimal montantEnAttente;

}
