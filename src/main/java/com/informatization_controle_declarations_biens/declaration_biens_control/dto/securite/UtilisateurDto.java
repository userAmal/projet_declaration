package com.informatization_controle_declarations_biens.declaration_biens_control.dto.securite;


import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.securite.UtilisateurProjection;

import lombok.Data;

@Data
public class UtilisateurDto {



    public UtilisateurDto() {
        super();
    }

    public UtilisateurDto(UtilisateurProjection utilisateurProjection) {
        this.id = utilisateurProjection.getId();
        this.email = utilisateurProjection.getEmail();
        this.firstname = utilisateurProjection.getFirstname();
        this.lastname = utilisateurProjection.getLastname();
        this.role = utilisateurProjection.getRole(); 
        this.statutEmploi = utilisateurProjection.getStatutEmploi();
    }

    public UtilisateurDto(Long id, String email, String lastname,String firstname, String password, String tel, RoleEnum role, 
                          Boolean statutEmploi) {
        this.id = id;
        this.email = email;
        this.lastname = lastname;
        this.firstname = firstname;
        this.password = password;
        this.tel = tel;
        this.role = role;
        this.statutEmploi = statutEmploi;
    }

    public UtilisateurDto(UtilisateurProjection utilisateurProjection, String token) {
        this.id = utilisateurProjection.getId();
        this.email = utilisateurProjection.getEmail();
        this.firstname = utilisateurProjection.getFirstname();
        this.lastname = utilisateurProjection.getLastname();
        this.role = utilisateurProjection.getRole(); 
        this.statutEmploi = utilisateurProjection.getStatutEmploi();
        this.firstLogin = utilisateurProjection.getFirstLogin();

        this.token = token;
    }

    public UtilisateurDto(Utilisateur utilisateur, String token) {
        this.id = utilisateur.getId();
        this.email = utilisateur.getEmail();
        this.lastname = utilisateur.getLastname();
        this.firstname = utilisateur.getFirstname();
        this.password = utilisateur.getPassword();
        this.tel = utilisateur.getTel();
        this.role = utilisateur.getRole(); 
        this.statutEmploi = utilisateur.isStatutEmploi();
        this.firstLogin = utilisateur.getFirstLogin();

        this.token = token;
    }


    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private String tel;
    private RoleEnum role; 
    private String token;
    private Boolean statutEmploi;
    private Boolean firstLogin;


}
