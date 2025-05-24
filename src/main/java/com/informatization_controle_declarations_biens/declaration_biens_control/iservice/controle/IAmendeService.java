package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.controle.AmendeDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Amende;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.StatutAmendeEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.controle.AmendeProjection;

public interface IAmendeService {
    
    // Méthodes CRUD de base
    Amende save(Amende amende);
    Optional<Amende> findById(Long id);
    List<Amende> findAll();
    void deleteById(Long id);
    
    // Méthodes spécifiques
    List<Amende> findByDeclarationId(Long declarationId);
    List<Amende> findByAssujettiId(Long assujettiId);
    List<AmendeProjection> findAllAmendeProjections();
    List<Amende> findByStatut(StatutAmendeEnum statut);
    List<Amende> findByDateAmendeBetween(LocalDate debut, LocalDate fin);
    
    // Méthodes pour la gestion des paiements
    void payerAmende(Long amendeId, String referencePaiement);
    void annulerAmende(Long amendeId, String motifAnnulation);
    
    // Conversion DTO
    AmendeDTO toDTO(Amende amende);
    List<AmendeDTO> toDTOList(List<Amende> amendes);
}
