package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IAssujettiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Assujetti;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IAssujettiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.AssujettiProjection;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssujettiService implements IAssujettiService {

    @Autowired
    private IAssujettiData assujettiData;
    @Autowired
private IUtilisateurService utilisateurService;
public Assujetti save(Assujetti assujetti) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Utilisateur admin = utilisateurService.findByEmail(username)
        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    assujetti.setAdministrateur(admin);
    assujetti.setDatePriseDeService(new Date());
    return assujettiData.save(assujetti);
}


    @Override
    public Optional<Assujetti> findById(Long id) {
        return assujettiData.findById(id);
    }

    @Override
    public List<Assujetti> findAll() {
        return assujettiData.findAll();
    }

    @Override
    public void deleteById(Long id) {
        assujettiData.deleteById(id);
    }

    @Override
    public List<Assujetti> findByCode(String code) {
        
        return assujettiData.findByCode(code);
    }
    


    @Override
    public List<Assujetti> findByNom(String nom) {
        return assujettiData.findByNom(nom);
    }

    @Override
    public List<AssujettiProjection> getAssujettiDetails(Long id) {
        return assujettiData.getAssujettiDetails(id); 
    }
}
