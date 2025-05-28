package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.adminstat;

import java.util.Map;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatsDTO {
    private long totalUsers;
    private long activeUsers;
    private long archivedUsers;
    private Map<RoleEnum, Long> usersByRole;
}