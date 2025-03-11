package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration;


import java.sql.Date;
import java.util.List;
import java.util.Optional;


import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IGenericService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AssujettiProjection;


public interface IAssujettiService extends IGenericService<Assujetti, Long> {
    List<Assujetti> findAll();
    Optional<Assujetti> findById(Long id);
    Assujetti save(Assujetti assujetti);
    void deleteById(Long id);
    List<Assujetti> findByCode(String code);
    List<Assujetti> findByNom(String nom);
    List<Assujetti> findByEmail(String email);
//  List<Assujetti> findByFonction(String fonctionLibelle) ;
//  List<Assujetti> findByInstitution(String institutionLibelle);
    List<Assujetti> findByDatePriseDeServiceBetween(Date startDate, Date endDate);
    List<AssujettiProjection> getAssujettiDetails(Long id);

    
}

