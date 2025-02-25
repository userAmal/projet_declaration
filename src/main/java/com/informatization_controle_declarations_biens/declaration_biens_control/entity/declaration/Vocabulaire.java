package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Vocabulaire {

    public Vocabulaire() {
    }

    public Vocabulaire(Long id) {
        this.id = id;
    }

    public Vocabulaire(String intitule) {
        this.intitule = intitule;
    }

    public Vocabulaire(Long id, String intitule, TypeVocabulaire typevocabulaire) {
        this.id = id;
        this.intitule = intitule;
        this.typevocabulaire = typevocabulaire;
    }

    public Vocabulaire( String intitule, TypeVocabulaire typevocabulaire) {
        this.intitule = intitule;
        this.typevocabulaire = typevocabulaire;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 100, nullable = false)
    private String intitule;

    @ManyToOne
    @JoinColumn(nullable = false)
    private TypeVocabulaire typevocabulaire;

    @ManyToOne
    private Vocabulaire vocabulaireParent;
}