package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.DisponibilitesEnBanque;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.DisponibilitesEnBanqueProjection;

import lombok.Data;

@Data
public class DisponibilitesEnBanqueDto {

    public DisponibilitesEnBanqueDto() {
    }

    public DisponibilitesEnBanqueDto(DisponibilitesEnBanqueProjection projection) {
        this.id = projection.getId();
        this.banque = projection.getBanque();
        this.numeroCompte = projection.getNumeroCompte();
        this.typeCompte = projection.getTypeCompte();
        this.soldeFCFA = projection.getSoldeFCFA();
        this.dateSolde = projection.getDateSolde();
        this.dateCreation = projection.getDateCreation();
        this.isSynthese = projection.isSynthese();
        this.idDeclaration = projection.getIdDeclaration();
    }
    public DisponibilitesEnBanqueDto(DisponibilitesEnBanque disponibilitesEnBanque) {
        this.id = disponibilitesEnBanque.getId();
        this.banque = disponibilitesEnBanque.getBanque();
        this.numeroCompte = disponibilitesEnBanque.getNumeroCompte();
        this.typeCompte = disponibilitesEnBanque.getTypeCompte();
        this.soldeFCFA = disponibilitesEnBanque.getSoldeFCFA();
        this.dateSolde = disponibilitesEnBanque.getDateSolde();
        this.dateCreation = disponibilitesEnBanque.getDateCreation();
        this.isSynthese = disponibilitesEnBanque.isSynthese();
        this.idDeclaration = disponibilitesEnBanque.getIdDeclaration();
    }
    private Long id;
    private Vocabulaire banque;
    private String numeroCompte;
    private Vocabulaire typeCompte;
    private float soldeFCFA;
    private LocalDate dateSolde;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;
}
