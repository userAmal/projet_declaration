package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Animaux {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String especes;

    @Column(nullable = false)
    private int nombreTetes;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire modeAcquisition;

    @Column(nullable = false)
    private int anneeAcquisition;

    @Column(nullable = false)
    private float valeurAcquisition;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire localite;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @Column(nullable = false)
    private boolean isSynthese;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Declaration idDeclaration;
}
