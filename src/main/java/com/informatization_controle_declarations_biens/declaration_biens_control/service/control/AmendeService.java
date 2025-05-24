package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.AmendeData;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.controle.AmendeDTO;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Amende;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.StatutAmendeEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle.IAmendeService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.controle.AmendeProjection;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AmendeService implements IAmendeService {
    
    @Autowired
    private AmendeData amendeData;
    

    @Override
    public Amende save(Amende amende) {
        return amendeData.save(amende);
    }
    
    @Override
    public Optional<Amende> findById(Long id) {
        return amendeData.findById(id);
    }
    
    @Override
    public List<Amende> findAll() {
        return amendeData.findAll();
    }
    
    @Override
    public void deleteById(Long id) {
        amendeData.deleteById(id);
    }
    
    @Override
    public List<Amende> findByDeclarationId(Long declarationId) {
        return amendeData.findByDeclarationId(declarationId);
    }
    
    @Override
    public List<Amende> findByAssujettiId(Long assujettiId) {
        return amendeData.findByAssujettiId(assujettiId);
    }
    
    @Override
    public List<AmendeProjection> findAllAmendeProjections() {
        return amendeData.findAllAmendeProjections();
    }
    
    @Override
    public List<Amende> findByStatut(StatutAmendeEnum statut) {
        return amendeData.findByStatut(statut);
    }
    
    @Override
    public List<Amende> findByDateAmendeBetween(LocalDate debut, LocalDate fin) {
        return amendeData.findByDateAmendeBetween(debut, fin);
    }
    
    @Override
    @Transactional
    public void payerAmende(Long amendeId, String referencePaiement) {
        Amende amende = findById(amendeId)
            .orElseThrow(() -> new RuntimeException("Amende non trouvée"));
            
        amende.setStatut(StatutAmendeEnum.Payee);
        amende.setDatePaiement(LocalDate.now());
        amende.setReferencePaiement(referencePaiement);
        
        save(amende);
    }
    
    @Override
    @Transactional
    public void annulerAmende(Long amendeId, String motifAnnulation) {
        Amende amende = findById(amendeId)
            .orElseThrow(() -> new RuntimeException("Amende non trouvée"));
            
        amende.setStatut(StatutAmendeEnum.Annulee);
        amende.setMotif(motifAnnulation);
        
        save(amende);
    }
    
    @Override
    public AmendeDTO toDTO(Amende amende) {
        if (amende == null) {
            return null;
        }
        
        AmendeDTO dto = new AmendeDTO();
        dto.setId(amende.getId());
        dto.setDeclarationId(amende.getDeclaration().getId());
        
        // Récupérer le nom de l'assujetti associé
        Declaration declaration = amende.getDeclaration();
        if (declaration != null && declaration.getAssujetti() != null) {
            dto.setNomAssujetti(declaration.getAssujetti().getNom() + " " + declaration.getAssujetti().getPrenom());
        }
        
        dto.setDateAmende(amende.getDateAmende());
        dto.setMontant(amende.getMontant());
        dto.setStatut(amende.getStatut());
        dto.setMotif(amende.getMotif());
        dto.setDatePaiement(amende.getDatePaiement());
        dto.setReferencePaiement(amende.getReferencePaiement());
        
        return dto;
    }
    
    @Override
    public List<AmendeDTO> toDTOList(List<Amende> amendes) {
        return amendes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
