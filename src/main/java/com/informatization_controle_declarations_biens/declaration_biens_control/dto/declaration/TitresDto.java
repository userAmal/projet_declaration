
package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vocabulaire;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TitresProjection;
import lombok.Data;

@Data
public class TitresDto {
    private Long id;
    private Vocabulaire designationNatureActions;
    private float valeurEmplacement;
    private Vocabulaire emplacement;
    private Vocabulaire autrePrecisions;
    private LocalDate dateCreation;
    private boolean isSynthese;
    private Declaration idDeclaration;

    public TitresDto() {}

    public TitresDto(TitresProjection titresProjection) {
        this.id = titresProjection.getId();
        this.designationNatureActions = titresProjection.getDesignationNatureActions();
        this.valeurEmplacement = titresProjection.getValeurEmplacement();
        this.emplacement = titresProjection.getEmplacement();
        this.autrePrecisions = titresProjection.getAutrePrecisions();
        this.dateCreation = titresProjection.getDateCreation();
        this.isSynthese = titresProjection.isSynthese();
        this.idDeclaration = titresProjection.getIdDeclaration();
    }
}
