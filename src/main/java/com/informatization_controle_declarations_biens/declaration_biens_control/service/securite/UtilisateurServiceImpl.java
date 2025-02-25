package com.informatization_controle_declarations_biens.declaration_biens_control.service.securite;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.securite.IUtilisateurData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UtilisateurServiceImpl implements IUtilisateurService {

    @Autowired
    private final IUtilisateurData utilisateurData; 

    public UtilisateurServiceImpl(IUtilisateurData utilisateurData) {
        this.utilisateurData = utilisateurData;
    }

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurData.save(utilisateur); 
    }

    @Override
    public Optional<Utilisateur> findById(Long id) {
        return utilisateurData.findById(id); 
    }

    @Override
    public List<Utilisateur> findAll() {
        return utilisateurData.findAll(); 
    }

    @Override
    public void deleteById(Long id) {
        utilisateurData.deleteById(id); 
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurData.findByEmail(email);
    }
}
