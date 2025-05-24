package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsByUserDTO {
    private String username;
    private long totalDeclarations;
    private long acceptees;
    private long refusees;
}