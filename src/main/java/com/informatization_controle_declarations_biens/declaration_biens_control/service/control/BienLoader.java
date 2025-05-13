/* package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.AnimauxService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.AppareilsElectroMenagersService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.AutresBiensDeValeurService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.AutresDettesService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.DisponibilitesEnBanqueService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.EmpruntsService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.EspecesService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.FoncierBatiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.FoncierNonBatiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.LesCreancesService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.MeublesMeublantsService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.RevenusService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.TitresService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration.VehiculeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BienLoader {
    private final VehiculeService vehiculeRepository;
    private final FoncierBatiService foncierBatiRepository;
    private final FoncierNonBatiService foncierNonBatiRepository;
    private final RevenusService revenuRepository;
    private final EmpruntsService empruntRepository;
    private final DisponibilitesEnBanqueService disponibiliteBanqueRepository;
    private final EspecesService especeRepository;
    private final AnimauxService animalRepository;
    private final MeublesMeublantsService meubleMeublantRepository;
    private final TitresService titreRepository;
    private final LesCreancesService creanceRepository;
    private final AutresDettesService autreDetteRepository;
    private final AutresBiensDeValeurService autreBienValeurRepository;
    private final AppareilsElectroMenagersService appareilElectromenagerRepository;

    public Map<String, Object> loadAllBiens(Long declarationId) {
        Map<String, Object> biens = new HashMap<>();
        
        biens.put("vehicules", vehiculeRepository.getByDeclaration(declarationId));
        biens.put("fonciersBatis", foncierBatiRepository.getByDeclaration(declarationId));
        biens.put("fonciersNonBatis", foncierNonBatiRepository.getByDeclaration(declarationId));
        biens.put("revenus", revenuRepository.getByDeclaration(declarationId));
        biens.put("emprunts", empruntRepository.getByDeclaration(declarationId));
        biens.put("disponibilitesBanque", disponibiliteBanqueRepository.getByDeclaration(declarationId));
        biens.put("especes", especeRepository.getByDeclaration(declarationId));
        biens.put("animaux", animalRepository.getAnimauxByDeclaration(declarationId));
        biens.put("meublesMeublants", meubleMeublantRepository.getByDeclaration(declarationId));
        biens.put("titres", titreRepository.getByDeclaration(declarationId));
        biens.put("creances", creanceRepository.getByDeclaration(declarationId));
        biens.put("autresDettes", autreDetteRepository.getByDeclaration(declarationId));
        biens.put("autresBiensDeValeur", autreBienValeurRepository.getByDeclaration(declarationId));
        biens.put("appareilsElectromenagers", appareilElectromenagerRepository.getByDeclaration(declarationId));
        
        return biens;
    }
} */