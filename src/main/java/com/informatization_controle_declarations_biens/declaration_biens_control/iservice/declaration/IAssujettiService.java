package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;


import java.util.List;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AssujettiProjection;

public interface IAssujettiService extends IGenericService<Assujetti, Long> {
    List<Assujetti> findByCode(String code);
    List<Assujetti> findByNom(String nom);
    List<AssujettiProjection> getAssujettiDetails(Long id);
    
}

