package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VocabularyStatsDTO {
    private long totalTerms;
    private Map<String, Long> termsByType; // Par type de vocabulaire
}