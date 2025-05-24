package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAnimauxData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAppareilsElectroMenagersData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAutresBiensDeValeurData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAutresDettesData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDisponibilitesEnBanqueData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IEmpruntsData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IEspecesData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IFoncierBatiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IFoncierNonBatiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.ILesCreancesData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IMeublesMeublantsData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IRevenusData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.ITitreData;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IVehiculeData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class DeclarationDtoLoader {
    private final IDeclarationData declarationData;
    private final IVehiculeData vehiculeData;
    private final IRevenusData revenuData;
    private final IAnimauxData animauxData;
    private final IAppareilsElectroMenagersData appareilsElectroMenagersData;
    private final IAutresBiensDeValeurData autresBiensDeValeurData;
    private final IAutresDettesData autresDettesData;
    private final IDisponibilitesEnBanqueData disponibilitesEnBanqueData;
    private final IEmpruntsData empruntsData;
    private final IEspecesData especesData;
    private final IFoncierBatiData foncierBatiData;
    private final IFoncierNonBatiData foncierNonBatiData;
    private final ILesCreancesData lesCreancesData;
    private final IMeublesMeublantsData meublesMeublantsData;
    private final ITitreData titreData;



    public DeclarationDto loadFullDeclarationDto(Long declarationId) {
        // Chargez l'entité Declaration avec les relations de base
        Declaration declaration = declarationData.getFullDeclarationDetails(declarationId)
            .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));
        
        // Créez le DTO à partir de l'entité
        DeclarationDto dto = new DeclarationDto(declaration);
        
        // Chargez les projections spécifiques
        dto.setVehicules(vehiculeData.findByIdDeclaration_Id(declarationId));
        dto.setRevenus(revenuData.findByDeclarationId(declarationId));
        dto.setAnimaux(animauxData.findByIdDeclaration_Id(declarationId));
        dto.setAppareilsElectromenagers(appareilsElectroMenagersData.findByIdDeclaration_Id(declarationId));
        dto.setAutresBiensDeValeur(autresBiensDeValeurData.findByDeclarationId(declarationId));
        dto.setAutresDettes(autresDettesData.findByIdDeclaration_Id(declarationId));
        dto.setDisponibilitesBanque(disponibilitesEnBanqueData.findByIdDeclaration_Id(declarationId));
        dto.setEmprunts(empruntsData.findByIdDeclaration_Id(declarationId));
        dto.setEspeces(especesData.findByIdDeclaration_Id(declarationId));
        dto.setFonciersBatis(foncierBatiData.findByIdDeclaration_Id(declarationId));
        dto.setFonciersNonBatis(foncierNonBatiData.findByDeclarationId(declarationId));
        dto.setCreances(lesCreancesData.findByDeclarationId(declarationId));
        dto.setMeublesMeublants(meublesMeublantsData.findByDeclarationId(declarationId));
        dto.setTitres(titreData.findByDeclarationId(declarationId));

        // ... chargez toutes les autres listes
        
        return dto;
    }
}