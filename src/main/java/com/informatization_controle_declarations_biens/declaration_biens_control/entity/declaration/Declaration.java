package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Declaration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dateDeclaration;



    @ManyToOne
    @JoinColumn(nullable = false)
    private Assujetti assujetti;
    
    @Column(nullable = false)
    private TypeDeclarationEnum typeDeclaration;

    @Column(nullable = false)
    private EtatDeclarationEnum etatDeclaration;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
}

