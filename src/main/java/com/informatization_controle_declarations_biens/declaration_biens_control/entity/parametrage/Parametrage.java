package com.informatization_controle_declarations_biens.declaration_biens_control.entity.parametrage;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "parametrage")
public class Parametrage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String code; // Clé unique du paramètre

    @Column(nullable = false, length = 255)
    private String description; // Description du paramètre

    @Column(nullable = false, length = 500)
    private String valeur; // Valeur du paramètre


    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
}
