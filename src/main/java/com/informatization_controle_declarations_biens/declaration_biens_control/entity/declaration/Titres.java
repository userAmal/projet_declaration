package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Titres {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire designationNatureActions;

    private float valeurEmplacement;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire emplacement;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire autrePrecisions;

    private LocalDate dateCreation;
    private boolean isSynthese;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Declaration idDeclaration;
}
