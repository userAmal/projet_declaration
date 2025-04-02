package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;



import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDeclarationService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class DeclarationService implements IDeclarationService {
    
    private final IDeclarationData declarationData;
    private final AnimauxService animauxService;
    private final AppareilsElectroMenagersService appareilElectroMenagerService;
    private final AutresBiensDeValeurService autresBiensDeValeurService;
    private final AutresDettesService autresDettesService;
    private final DisponibilitesEnBanqueService disponibilitesEnBanqueService;
    private final EmpruntsService empruntsService;
    private final EspecesService especesService;
    private final FoncierBatiService foncierBatiService;
    private final FoncierNonBatiService foncierNonBatiService;
    private final LesCreancesService lesCreancesService;
    private final MeublesMeublantsService meublesMeublantsService;
    private final RevenusService revenusService;
    private final TitresService titresService;
    private final VehiculeService vehiculeService;
    
    public DeclarationService(IDeclarationData declarationData,
                             AnimauxService animauxService,
                             AppareilsElectroMenagersService appareilElectroMenagerService,
                             AutresBiensDeValeurService autresBiensDeValeurService,
                             AutresDettesService autresDettesService,
                             DisponibilitesEnBanqueService disponibilitesEnBanqueService,
                             EmpruntsService empruntsService,
                             EspecesService especesService,
                             FoncierBatiService foncierBatiService,
                             FoncierNonBatiService foncierNonBatiService,
                             LesCreancesService lesCreancesService,
                             MeublesMeublantsService meublesMeublantsService,
                             RevenusService revenusService,
                             TitresService titresService,
                             VehiculeService vehiculeService) {
        this.declarationData = declarationData;
        this.animauxService = animauxService;
        this.appareilElectroMenagerService = appareilElectroMenagerService;
        this.autresBiensDeValeurService = autresBiensDeValeurService;
        this.autresDettesService = autresDettesService;
        this.disponibilitesEnBanqueService = disponibilitesEnBanqueService;
        this.empruntsService = empruntsService;
        this.especesService = especesService;
        this.foncierBatiService = foncierBatiService;
        this.foncierNonBatiService = foncierNonBatiService;
        this.lesCreancesService = lesCreancesService;
        this.meublesMeublantsService = meublesMeublantsService;
        this.revenusService = revenusService;
        this.titresService = titresService;
        this.vehiculeService = vehiculeService;
    }
        
    public DeclarationDto getFullDeclarationDetails(Long declarationId) {
        DeclarationDto dto = new DeclarationDto();
        
        Declaration declaration = declarationData.findById(declarationId).orElse(null);
        if (declaration == null) return null;
        
        dto = new DeclarationDto(declaration);
        
        dto.setAnimaux(animauxService.getAnimauxByDeclaration(declarationId));
        System.out.println(dto.getAnimaux());

        dto.setVehicules(vehiculeService.getByDeclaration(declarationId));
        dto.setFonciersBatis(foncierBatiService.getByDeclaration(declarationId));
        dto.setFonciersNonBatis(foncierNonBatiService.getByDeclaration(declarationId));
        dto.setAppareilsElectromenagers(appareilElectroMenagerService.getByDeclaration(declarationId));
        dto.setAutresBiensDeValeur(autresBiensDeValeurService.getByDeclaration(declarationId));
        dto.setDisponibilitesBanque(disponibilitesEnBanqueService.getByDeclaration(declarationId));
        dto.setEmprunts(empruntsService.getByDeclaration(declarationId));
        dto.setEspeces(especesService.getByDeclaration(declarationId));
        dto.setCreances(lesCreancesService.getByDeclaration(declarationId));
        dto.setMeublesMeublants(meublesMeublantsService.getByDeclaration(declarationId));
        dto.setRevenus(revenusService.getByDeclaration(declarationId));
        dto.setTitres(titresService.getByDeclaration(declarationId));
        dto.setAutresDettes(autresDettesService.getByDeclaration(declarationId));
        
        return dto;
    }


    @Override
    public Optional<Declaration> findById(Long id) {
        return declarationData.findById(id);
    }
    
    @Override
    public List<Declaration> findAll() {
        return declarationData.findAll();
    }
    
    @Override
    public Declaration save(Declaration entity) {
        return declarationData.save(entity);
    }
    
    @Override
    public void deleteById(Long id) {
        declarationData.deleteById(id);
    }
    @Override
public Declaration validateDeclaration(Long id) {
    Declaration declaration = declarationData.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Déclaration non trouvée"));

    
    return declarationData.save(declaration);
}

@Override
public Declaration refuseDeclaration(Long id) {
    Declaration declaration = declarationData.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Déclaration non trouvée"));
    
    
    return declarationData.save(declaration);
}
}