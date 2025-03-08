package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Emprunts;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EmpruntsProjection;
import lombok.Data;

@Data
public class EmpruntsDto {
    private Long id;
    private Vocabulaire institutionsFinancieres;
    private String numeroCompte;
    private Vocabulaire typeEmprunt;
    private float montantEmprunt;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;

    public EmpruntsDto() {}

    public EmpruntsDto(EmpruntsProjection empruntsProjection) {
        this.id = empruntsProjection.getId();
        this.institutionsFinancieres = empruntsProjection.getInstitutionsFinancieres();
        this.numeroCompte = empruntsProjection.getNumeroCompte();
        this.typeEmprunt = empruntsProjection.getTypeEmprunt();
        this.montantEmprunt = empruntsProjection.getMontantEmprunt();
        this.dateCreation = empruntsProjection.getDateCreation();
        this.isSynthese = empruntsProjection.isSynthese();
        this.idDeclaration = empruntsProjection.getIdDeclaration();
    }
    public EmpruntsDto(Emprunts emprunts) {
        this.id = emprunts.getId();
        this.institutionsFinancieres = emprunts.getInstitutionsFinancieres();
        this.numeroCompte = emprunts.getNumeroCompte();
        this.typeEmprunt = emprunts.getTypeEmprunt();
        this.montantEmprunt = emprunts.getMontantEmprunt();
        this.dateCreation = emprunts.getDateCreation();
        this.isSynthese = emprunts.isSynthese();
        this.idDeclaration = emprunts.getIdDeclaration();
    }
}
