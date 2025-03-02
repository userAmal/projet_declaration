package com.informatization_controle_declarations_biens.declaration_biens_control.projection.securite;


import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;

public interface UtilisateurProjection {

    Long getId(); 
    String getEmail(); 
    String getFirstname(); 
    String getLastname(); 
    String getTel(); 
    RoleEnum getRole(); 
    Boolean getStatutEmploi(); 
    Boolean getFirstLogin(); 

}

