package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutresBiensDeValeur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire designation;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire localite;

    @Column(nullable = false)
    private int anneeAcquis;

    @Column(nullable = false)
    private float valeurAcquisition;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire autrePrecisions;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire type;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dateCreation;

    @Column(nullable = false)
    private boolean isSynthese;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Declaration idDeclaration;
}
