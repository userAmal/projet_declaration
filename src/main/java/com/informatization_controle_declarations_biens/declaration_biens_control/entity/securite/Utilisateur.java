package com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
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

    @Column(length = 200, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String lastname;

    
    @Column(length = 100, nullable = false)
    private String firstname;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 30, nullable = false, unique = true)
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;  
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // À modifier si tu veux gérer l'expiration des comptes
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // À modifier si tu veux gérer le verrouillage des comptes
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // À modifier si tu veux gérer l'expiration des mots de passe
    }

    @Override
    public boolean isEnabled() {
        return true; // Peut être remplacé par un champ `boolean actif`
    }
}
