package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAutresBiensDeValeurData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresBiensDeValeur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAutresBiensDeValeurService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresBiensDeValeurProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutresBiensDeValeurService implements IAutresBiensDeValeurService {

    private final IAutresBiensDeValeurData autresBiensDeValeurData;

    public AutresBiensDeValeurService(IAutresBiensDeValeurData autresBiensDeValeurData) {
        this.autresBiensDeValeurData = autresBiensDeValeurData;
    }

    @Override
    public List<AutresBiensDeValeurProjection> getByDeclaration(Long declarationId) {
        return autresBiensDeValeurData.findByDeclarationId(declarationId);
    }

       @Override
    public Optional<AutresBiensDeValeur> findById(Long id) {
        return autresBiensDeValeurData.findById(id);
    }

    @Override
    public AutresBiensDeValeur save(AutresBiensDeValeur entity) {
        return autresBiensDeValeurData.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        autresBiensDeValeurData.deleteById(id);
    }
    
}
