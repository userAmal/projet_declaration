package com.informatization_controle_declarations_biens.declaration_biens_control.controller.securite;


import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private RoleEnum Role;
    private boolean statut_emploi;
    private String tel;
}
