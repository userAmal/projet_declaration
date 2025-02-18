package com.informatization_controle_declarations_biens.declaration_biens_control.service.securite;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.securite.IUtilisateurData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite.IUtilisateurService;

@Service
public class UtilisateurServiceImpl implements IUtilisateurService {

    @Autowired
    private final IUtilisateurData utilisateurData; // Changer le type pour IUtilisateurData

    public UtilisateurServiceImpl(IUtilisateurData utilisateurData) {
        this.utilisateurData = utilisateurData;
    }

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurData.save(utilisateur); // Appel au repository
    }

    @Override
    public Optional<Utilisateur> findById(Long id) {
        return utilisateurData.findById(id); // Appel au repository
    }

    @Override
    public List<Utilisateur> findAll() {
        return utilisateurData.findAll(); // Appel au repository
    }

    @Override
    public void deleteById(Long id) {
        utilisateurData.deleteById(id); // Appel au repository
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurData.findByEmail(email); // Appel au repository
    }
}
