package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilitesEnBanque {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire banque;

    @Column(nullable = false)
    private String numeroCompte;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire typeCompte;

    @Column(nullable = false)
    private float soldeFCFA;

    @Column(nullable = false)
    private LocalDate dateSolde;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @Column(nullable = false)
    private boolean isSynthese;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Declaration DeclarationId;
}