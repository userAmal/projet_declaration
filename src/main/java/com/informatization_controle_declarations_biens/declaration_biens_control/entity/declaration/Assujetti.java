package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

@Data
@Entity
public class Assujetti {

    public Assujetti() {
    }

    public Assujetti(Long id) {
        this.id = id;
    }

    public Assujetti( Vocabulaire civilite, String nom, String prenom, String contacttel, String email, 
                     Vocabulaire institutions, Vocabulaire administration, Vocabulaire entite, String code,
                     Vocabulaire fonction, String matricule, Date datePriseDeService,EtatAssujettiEnum etat,Utilisateur administrateur) {
      
        this.civilite = civilite;
        this.nom = nom;
        this.prenom = prenom;
        this.contacttel = contacttel;
        this.email = email;
        this.institutions = institutions;
        this.administration = administration;
        this.entite = entite;
        this.fonction = fonction;
        this.matricule = matricule;
        this.code=code;
        this.etat=etat;
        this.datePriseDeService = datePriseDeService;
        this.administrateur = administrateur;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


@ManyToOne
@JoinColumn
private Vocabulaire civilite;


    @Column(length = 100, nullable = false)
    private String nom;  

    @Column(length = 100, nullable = false)
    private String prenom;  

    @Column(length = 20, nullable = false, unique = true)
    private String contacttel; 

    @Column(length = 100, nullable = false)
    private String email; 

    @Column(length = 100, nullable = false, unique = true)
    private String code;  

    @Column(nullable = false)
    private EtatAssujettiEnum etat;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire institutions;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire administration; 

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire entite; 

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire fonction;

    @Column(length = 50, nullable = false)
    private String matricule; 

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date datePriseDeService;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Utilisateur administrateur; // Ajout de l'administrat
}
