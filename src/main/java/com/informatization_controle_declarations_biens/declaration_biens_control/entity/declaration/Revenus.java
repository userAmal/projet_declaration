package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Revenus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private float salaireMensuelNet;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire autresRevenus;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dateCreation;
}
