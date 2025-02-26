package com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration;


import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatAssujettiEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

import java.util.Date;

public interface AssujettiProjection {

    Long getId();

    Vocabulaire getCivilite();

    String getNom();

    String getPrenom();

    String getContacttel();

    String getCode();

    String getEmail();

    EtatAssujettiEnum getEtat();

    Vocabulaire getInstitutions();

    Vocabulaire getAdministration();

    Vocabulaire getEntite();

    Vocabulaire getFonction();

    String getMatricule();

    Date getDatePriseDeService();

    Date getDateCessationFonction();

    Utilisateur getAdministrateur();

}
