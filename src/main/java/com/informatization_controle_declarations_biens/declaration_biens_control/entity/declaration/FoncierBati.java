package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoncierBati {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire nature;

    @Column(nullable = false)
    private int anneeConstruction;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire modeAcquisition;

    @Column(nullable = false)
    private String referencesCadastrales;

    @Column(nullable = false)
    private String superficie;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire localis;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire typeUsage;

    @Column(nullable = false)
    private float coutAcquisitionFCFA;

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
