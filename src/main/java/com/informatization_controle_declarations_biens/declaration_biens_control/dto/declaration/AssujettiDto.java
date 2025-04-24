package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.util.Date;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatAssujettiEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AssujettiProjection;
import lombok.Data;

@Data
public class AssujettiDto {

    public AssujettiDto() {
    }

public AssujettiDto(Assujetti assujetti) {
    this.id = assujetti.getId();
    this.civilite = assujetti.getCivilite();
    this.nom = assujetti.getNom();
    this.prenom = assujetti.getPrenom();
    this.contact = assujetti.getContacttel();
    this.code = assujetti.getCode();
    this.email = assujetti.getEmail();
    this.institutions = assujetti.getInstitutions();  
    this.administration = assujetti.getAdministration(); 
    this.entite = assujetti.getEntite(); 
    this.fonction = assujetti.getFonction();
    this.matricule = assujetti.getMatricule();
    this.etat = assujetti.getEtat();
    this.datePriseDeService = assujetti.getDatePriseDeService();
    this.dateCessationFonction = assujetti.getDateCessationFonction();
    this.administrateur = assujetti.getAdministrateur();
}

    public AssujettiDto(AssujettiProjection assujettiProjection) {
        this.id = assujettiProjection.getId();
        this.civilite = assujettiProjection.getCivilite();
        this.nom = assujettiProjection.getNom();
        this.prenom = assujettiProjection.getPrenom();
        this.contact = assujettiProjection.getContacttel();
        this.code = assujettiProjection.getCode();
        this.email = assujettiProjection.getEmail();
        this.institutions = assujettiProjection.getInstitutions();  
        this.administration = assujettiProjection.getAdministration(); 
        this.entite = assujettiProjection.getEntite(); 
        this.fonction = assujettiProjection.getFonction();
        this.matricule = assujettiProjection.getMatricule();
        this.etat = assujettiProjection.getEtat();
        this.datePriseDeService = assujettiProjection.getDatePriseDeService();
        this.dateCessationFonction = assujettiProjection.getDateCessationFonction();
        this.administrateur=assujettiProjection.getAdministrateur();
    }
    private Long id;
    private Vocabulaire civilite;
    private String nom;
    private String prenom;
    private String contact;
    private String code;
    private String email;
    private EtatAssujettiEnum etat;
    private Vocabulaire institutions;   
    private Vocabulaire administration; 
    private Vocabulaire entite;       
    private Vocabulaire fonction;
    private String matricule;
    private Date datePriseDeService;
    private Date dateCessationFonction;
    private Utilisateur administrateur;

}
