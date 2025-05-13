package com.informatization_controle_declarations_biens.declaration_biens_control.controller.securite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String token;
    private Long id;
    private String firstname;
    private String lastname;
}
