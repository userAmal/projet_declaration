package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeclarationTrendDTO {
    private String period;
    private long count;
}