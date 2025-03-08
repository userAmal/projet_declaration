package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAutresDettesData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.AutresDettes;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAutresDettesService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AutresDettesProjection;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AutresDettesService implements IAutresDettesService {

    private final IAutresDettesData data;

    public AutresDettesService(IAutresDettesData data) {
        this.data = data;
    }

    @Override
    public List<AutresDettesProjection> getByDeclaration(Long declarationId) {
        return data.findByIdDeclaration_Id(declarationId);
    }

    @Override
    public List<AutresDettes> findAll() {
        return data.findAll();
    }

    @Override
    public Optional<AutresDettes> findById(Long id) {
        return data.findById(id);
    }

    @Override
    public AutresDettes save(AutresDettes entity) {
        return data.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        data.deleteById(id);
    }
}
