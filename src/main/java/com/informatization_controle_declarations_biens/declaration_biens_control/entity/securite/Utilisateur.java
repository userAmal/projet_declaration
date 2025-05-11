package com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Entity
@Table(name="Utilisateur")
@Builder
@NoArgsConstructor 
@AllArgsConstructor 
public class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 200, nullable = false, unique = true)
    @Email(message = "L'email doit être valide.")
    @NotNull(message = "L'email est obligatoire.")
    private String email;

    @Column(length = 100, nullable = false)
    @NotBlank(message = "Le nom de famille est obligatoire.")
    private String lastname;

    
    @Column(length = 100, nullable = false)
    @NotBlank(message = "Le prénom est obligatoire.")
    private String firstname;

    @Column(length = 100, nullable = false)
    @NotBlank(message = "Le mot de passe est obligatoire.")
    private String password;

    @Column(length = 30, nullable = false, unique = true)
    @NotBlank(message = "Le téléphone est obligatoire.")
    private String tel;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Column(length = 50, nullable = false)
    @Builder.Default
    private boolean statutEmploi=true;

    @Column(name = "first_login")
    @Builder.Default
    private boolean firstLogin = true; 
    

    public Boolean getFirstLogin() {
        return firstLogin;
    }
    
    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }
    
@Override
@JsonIgnore
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
}



    @Override
    public String getUsername() {
        return email;  
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return true; 
    }
}
