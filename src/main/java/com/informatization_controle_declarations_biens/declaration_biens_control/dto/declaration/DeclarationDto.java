package com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration;

import java.time.LocalDate;
import java.util.List;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AnimauxProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AppareilsElectroMenagersProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresBiensDeValeurProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresDettesProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.DeclarationProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.DisponibilitesEnBanqueProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EmpruntsProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.EspecesProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierBatiProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierNonBatiProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.LesCreancesProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.MeublesMeublantsProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.RevenusProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.TitresProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VehiculeProjection;

import lombok.Data;

@Data
public class DeclarationDto {

    public DeclarationDto() {
    }

    public DeclarationDto(DeclarationProjection declarationProjection) {
        this.id = declarationProjection.getId();
        this.typeDeclaration = declarationProjection.getTypeDeclarationEnum();
        this.dateDeclaration = declarationProjection.getDateDeclaration();
        this.assujetti = declarationProjection.getAssujetti();
        this.etatDeclaration = declarationProjection.getEtatDeclarationEnum();
    }

    public DeclarationDto(Declaration declaration) {
        this.id = declaration.getId();
        this.typeDeclaration = declaration.getTypeDeclaration();
        this.dateDeclaration = declaration.getDateDeclaration();
        this.assujetti = declaration.getAssujetti();
        this.etatDeclaration = declaration.getEtatDeclaration();
    }

 

    private Long id;
    private LocalDate dateDeclaration;
private Assujetti assujetti;
    private TypeDeclarationEnum typeDeclaration;
    private EtatDeclarationEnum etatDeclaration;
     private List<AnimauxProjection> animaux;
    private List<VehiculeProjection> vehicules;
    private List<FoncierBatiProjection> fonciersBatis;
    private List<FoncierNonBatiProjection> fonciersNonBatis;
    private List<AppareilsElectroMenagersProjection> appareilsElectromenagers;
    private List<AutresBiensDeValeurProjection> autresBiensDeValeur;
    private List<DisponibilitesEnBanqueProjection> disponibilitesBanque;
    private List<EmpruntsProjection> emprunts;
    private List<EspecesProjection> especes;
    private List<LesCreancesProjection> creances;
    private List<MeublesMeublantsProjection> meublesMeublants;
    private List<RevenusProjection> revenus;
    private List<TitresProjection> titres;
    private List<AutresDettesProjection> autresDettes;

}
