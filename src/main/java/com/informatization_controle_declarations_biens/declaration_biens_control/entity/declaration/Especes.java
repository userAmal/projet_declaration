package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Especes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private float monnaie;

    @Column(nullable = false)
    private float devise;

    @Column(nullable = false)
    private float tauxChange;

    @Column(nullable = false)
    private float montantFCFA;

    @Column(nullable = false)
    private float montantTotalFCFA;

    @Column(nullable = false)
    private LocalDate dateEspece;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @Column(nullable = false)
    private boolean isSynthese;
}
