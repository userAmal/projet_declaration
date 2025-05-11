package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire designation;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire marque;

    @Column(nullable = false)
    private String immatriculation;

    @Column(nullable = false)
    private int anneeAcquisition;

    @Column(nullable = false)
    private float valeurAcquisition;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire etatGeneral;

    @Column(nullable = false)
    private double kilometrage;
    

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire carburant; 

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire transmission;


    @Column(nullable = false)
    private LocalDate dateCreation;

    @Column(nullable = false)
    private boolean isSynthese;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Declaration idDeclaration;
}
