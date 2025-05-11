/* package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;


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
                     Vocabulaire fonction, String matricule, Date datePriseDeService,EtatAssujettiEnum etat,Utilisateur administrateur,Date dateCessationFonction) {
      
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
        this.dateCessationFonction=dateCessationFonction;
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

    @Column(length = 100, nullable = false, unique = true)
    private String email; 

    @Column(length = 100, nullable = false, unique = true)
    private String code;  

    @Column(nullable = false)
    private EtatAssujettiEnum etat = EtatAssujettiEnum.NOUVEAU;  // Valeur par défaut

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

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dateCessationFonction;


    @ManyToOne
   @JoinColumn(nullable = false)
    private Utilisateur administrateur;
}
 */


package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Assujetti {

    public Assujetti() {
    }
    
    public Assujetti(Long id) {
        this.id = id;
    }
    
    public Assujetti(Vocabulaire civilite, String nom, String prenom, String contacttel, String email,
                    Vocabulaire institutions, Vocabulaire administration, Vocabulaire entite, String code,
                    Vocabulaire fonction, String matricule, Date datePriseDeService, EtatAssujettiEnum etat, 
                    Utilisateur administrateur, Date dateCessationFonction) {
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
        this.code = code;
        this.etat = etat;
        this.datePriseDeService = datePriseDeService;
        this.administrateur = administrateur;
        this.dateCessationFonction = dateCessationFonction;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "La civilité est obligatoire")
    @ManyToOne
    @JoinColumn
    private Vocabulaire civilite;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(length = 100, nullable = false)
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    @Column(length = 100, nullable = false)
    private String prenom;
    
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^\\d{8}$", message = "Le numéro de téléphone doit contenir exactement 8 chiffres")
    @Column(length = 20, nullable = false, unique = true)
    private String contacttel;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Veuillez fournir une adresse email valide")
    @Column(length = 100, nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Le code est obligatoire")
    @Column(length = 100, nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private EtatAssujettiEnum etat = EtatAssujettiEnum.NOUVEAU;  // Valeur par défaut

    
    @NotNull(message = "L'institution est obligatoire")
    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire institutions;
    
    @NotNull(message = "L'administration est obligatoire")
    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire administration;
    
    @NotNull(message = "L'entité est obligatoire")
    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire entite;
    
    @NotNull(message = "La fonction est obligatoire")
    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire fonction;
    
    @NotBlank(message = "Le matricule est obligatoire")
    @Size(min = 1, max = 50, message = "Le matricule doit contenir entre 1 et 50 caractères")
    @Column(length = 50, nullable = false)
    private String matricule;
    
    @NotNull(message = "La date de prise de service est obligatoire")
    @Past(message = "La date de prise de service doit être dans le passé")
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date datePriseDeService;
    
    @PastOrPresent(message = "La date de cessation de fonction doit être dans le passé ou le présent")
    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dateCessationFonction;
    

    @ManyToOne
   @JoinColumn(nullable = false)
    private Utilisateur administrateur;
} 

