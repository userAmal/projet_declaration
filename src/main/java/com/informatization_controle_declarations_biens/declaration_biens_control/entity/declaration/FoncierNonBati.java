package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoncierNonBati {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire nature;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire modeAcquisition;

    @Column(nullable = false)
    private String ilot;

    @Column(nullable = false)
    private String lotissement;

    @Column(nullable = false)
    private String superficie;

    @Column(nullable = false)
    private String localite;

    @Column(nullable = false)
    private String titrePropriete;

    @Column(nullable = false)
    private int dateAcquis;

    @Column(nullable = false)
    private float valeurAcquisFCFA;

    @Column(nullable = false)
    private float coutInvestissements;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @Column(nullable = false)
    private boolean isSynthese;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Declaration idDeclaration;
}
