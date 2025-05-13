package com.informatization_controle_declarations_biens.declaration_biens_control.controller.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.CommentaireGenerique;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.TypeEntiteEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.controle.ICommentaireGeneriqueService;

@RestController
@RequestMapping("/api/commentaires")
public class CommentaireGeneriqueController {

    @Autowired
    private ICommentaireGeneriqueService commentaireService;

    @GetMapping
    public ResponseEntity<List<CommentaireGenerique>> getAllCommentaires() {
        return ResponseEntity.ok(commentaireService.findAll());
    }

    @GetMapping("/declaration/{declarationId}/type/{typeEntite}")
    public ResponseEntity<List<CommentaireGenerique>> getCommentairesParDeclarationEtType(
            @PathVariable Long declarationId,
            @PathVariable TypeEntiteEnum typeEntite) {
        
        List<CommentaireGenerique> commentaires = commentaireService.getCommentairesParDeclarationEtType(declarationId, typeEntite);
        return ResponseEntity.ok(commentaires);
    }


    @GetMapping("/declaration/{declarationId}/utilisateur/{utilisateurId}/type/{typeEntite}")
    public ResponseEntity<List<CommentaireGenerique>> getCommentairesParUtilisateurEtDeclarationEtType(
            @PathVariable Long utilisateurId,
            @PathVariable Long declarationId,
            @PathVariable TypeEntiteEnum typeEntite) {

        List<CommentaireGenerique> commentaires = commentaireService.getCommentairesParUtilisateurEtDeclarationEtType(utilisateurId, declarationId, typeEntite);
        return ResponseEntity.ok(commentaires);
    }



    @GetMapping("/{id}")
    public ResponseEntity<CommentaireGenerique> getCommentaireById(@PathVariable Long id) {
        return commentaireService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CommentaireGenerique> createCommentaire(@RequestBody CommentaireGenerique commentaire) {
        CommentaireGenerique created = commentaireService.save(commentaire);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentaireGenerique> updateCommentaire(@PathVariable Long id, @RequestBody CommentaireGenerique commentaire) {
        // Récupérer le commentaire existant
        CommentaireGenerique existingCommentaire = commentaireService.findById(id)
                .orElseGet(() -> null);
    
        // Si le commentaire n'existe pas
        if (existingCommentaire == null) {
            return ResponseEntity.notFound().build();
        }
    
        // Ne pas modifier la déclaration et le typeEntite (garder ceux de l'existant)
        commentaire.setDeclaration(existingCommentaire.getDeclaration());
        commentaire.setTypeEntite(existingCommentaire.getTypeEntite());
    
        // Ne modifier que le commentaire
        existingCommentaire.setCommentaire(commentaire.getCommentaire());
    
        // Sauvegarder les modifications
        CommentaireGenerique updated = commentaireService.save(existingCommentaire);
        return ResponseEntity.ok(updated);
    }
    


   /*  @PutMapping("/{id}")
    public ResponseEntity<CommentaireGenerique> updateCommentaire(@PathVariable Long id, @RequestBody CommentaireGenerique commentaire) {
        CommentaireGenerique updated = commentaireService.updateCommentaire(id, commentaire);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    } */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommentaire(@PathVariable Long id) {
        commentaireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
