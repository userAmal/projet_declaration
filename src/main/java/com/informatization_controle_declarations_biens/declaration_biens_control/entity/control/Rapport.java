package com.informatization_controle_declarations_biens.declaration_biens_control.entity.control;


import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import java.time.LocalDateTime;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "rapports")
public class Rapport {
    public enum Type { PROVISOIRE, DEFINITIF }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private String reference; // Format: "RAPP-{type}-{idDeclaration}-{numSeq}"

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] contenuPdf;

    @Column(nullable = false)
    private String nomFichier; // Format: "{id}-{type}-{idDeclaration}.pdf"

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "declaration_id", nullable = false)
    private Declaration declaration;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column
    private Boolean decision;  // Uniquement pour les rapports définitifs

    @AssertTrue(message = "Un rapport définitif doit avoir une décision")
    public boolean isDecisionValide() {
        return type == Type.PROVISOIRE || decision != null;
    }

    @Column(nullable = false)
    private long tailleFichier;
}