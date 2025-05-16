package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data                     
@NoArgsConstructor        
@AllArgsConstructor       
@Builder                  
@Entity
@Table(name = "historique_declaration_users")
public class HistoriqueDeclarationUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "declaration_id", nullable = false)
    private Declaration declaration;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Utilisateur utilisateur;  
    
    @Column(name = "date_affectation", nullable = false)
    private LocalDate dateAffectation;
    
    @Column(name = "date_fin_affectation")
    private LocalDate dateFinAffectation;
}