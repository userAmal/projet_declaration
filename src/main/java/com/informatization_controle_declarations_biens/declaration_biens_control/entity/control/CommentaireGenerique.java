package com.informatization_controle_declarations_biens.declaration_biens_control.entity.control;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireGenerique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commentaire;

    @Enumerated(EnumType.STRING)
    private TypeEntiteEnum typeEntite;

    @ManyToOne
    private Utilisateur utilisateur;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dateComment;


    @ManyToOne
    private Declaration declaration;
}
