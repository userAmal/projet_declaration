package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.controle.ICommentaireGeneriqueData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.CommentaireGenerique;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.TypeEntiteEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IDeclarationData;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle.ICommentaireGeneriqueService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.UtilisateurServiceImpl;

@Service
public class CommentaireGeneriqueService implements ICommentaireGeneriqueService {

    @Autowired
    private ICommentaireGeneriqueData commentaireData;

    @Autowired
    private IDeclarationData declarationData;

    @Autowired
    private UtilisateurServiceImpl utilisateurServiceImpl;

    @Override
    public CommentaireGenerique ajouterCommentaire(Long declarationId, TypeEntiteEnum typeEntite, String commentaire, Long utilisateurId) {
        Declaration declaration = declarationData.findById(declarationId)
                .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));
        Utilisateur utilisateur = utilisateurServiceImpl.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        CommentaireGenerique c = new CommentaireGenerique();
        c.setCommentaire(commentaire);
        c.setTypeEntite(typeEntite);
        c.setDeclaration(declaration);
        c.setUtilisateur(utilisateur);

        return commentaireData.save(c);
    }

    @Override
    public CommentaireGenerique updateCommentaire(Long id, CommentaireGenerique commentaire) {
        return commentaireData.findById(id).map(existing -> {
            existing.setCommentaire(commentaire.getCommentaire());
            existing.setTypeEntite(commentaire.getTypeEntite());
            existing.setUtilisateur(commentaire.getUtilisateur());
            existing.setDeclaration(commentaire.getDeclaration());
            return commentaireData.save(existing);
        }).orElse(null);
    }

    @Override
    public List<CommentaireGenerique> getCommentairesParUtilisateurEtDeclarationEtType(Long utilisateurId, Long declarationId, TypeEntiteEnum typeEntite) {
        return commentaireData.findByUtilisateurIdAndDeclarationIdAndTypeEntite(utilisateurId, declarationId, typeEntite);
    }



    @Override
    public List<CommentaireGenerique> getCommentairesParDeclarationEtType(Long declarationId, TypeEntiteEnum typeEntite) {
        return commentaireData.findByDeclarationIdAndTypeEntite(declarationId, typeEntite);
    }

    // Implémentation des méthodes de IGenericService
    @Override
    public CommentaireGenerique save(CommentaireGenerique entity) {
        return commentaireData.save(entity);
    }

    @Override
    public List<CommentaireGenerique> findAll() {
        return commentaireData.findAll();
    }

    @Override
    public Optional<CommentaireGenerique> findById(Long id) {
        return commentaireData.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        commentaireData.deleteById(id);
    }
}
