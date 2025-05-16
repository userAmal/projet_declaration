package com.informatization_controle_declarations_biens.declaration_biens_control.controller.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.HistoriqueDeclarationUser;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.RoleEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IHistoriqueDeclarationUserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/historique-declaration")
public class HistoriqueDeclarationUserController {

    private final IHistoriqueDeclarationUserService historiqueService;

    public HistoriqueDeclarationUserController(IHistoriqueDeclarationUserService historiqueService) {
        this.historiqueService = historiqueService;
    }

    @PostMapping("/{declarationId}/utilisateur/{utilisateurId}")
    public ResponseEntity<HistoriqueDeclarationUser> createHistorique(
            @PathVariable Long declarationId,
            @PathVariable Long utilisateurId) {
        return ResponseEntity.ok(historiqueService.createHistorique(declarationId, utilisateurId));
    }

    @PutMapping("/{historiqueId}/close")
    public ResponseEntity<HistoriqueDeclarationUser> closeAffectation(
            @PathVariable Long historiqueId) {
        return ResponseEntity.ok(historiqueService.closeAffectation(historiqueId));
    }

    @GetMapping("/declaration/{declarationId}")
    public ResponseEntity<List<HistoriqueDeclarationUser>> getByDeclaration(
            @PathVariable Long declarationId) {
        return ResponseEntity.ok(historiqueService.getHistoriqueByDeclaration(declarationId));
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<HistoriqueDeclarationUser>> getByUtilisateur(
            @PathVariable Long utilisateurId) {
        return ResponseEntity.ok(historiqueService.getHistoriqueByUtilisateur(utilisateurId));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<HistoriqueDeclarationUser>> getByRole(
            @PathVariable RoleEnum role) {
        return ResponseEntity.ok(historiqueService.getHistoriqueByRole(role));
    }

    @GetMapping("/actives")
    public ResponseEntity<List<HistoriqueDeclarationUser>> getActiveAffectations() {
        return ResponseEntity.ok(historiqueService.getActiveAffectations());
    }

    @GetMapping("/declaration/{declarationId}/actives")
    public ResponseEntity<List<HistoriqueDeclarationUser>> getActiveAffectationsByDeclaration(
            @PathVariable Long declarationId) {
        return ResponseEntity.ok(historiqueService.getActiveAffectationsByDeclaration(declarationId));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsAffectation(
            @RequestParam Long declarationId,
            @RequestParam Long utilisateurId) {
        return ResponseEntity.ok(historiqueService.existsAffectation(declarationId, utilisateurId));
    }

    @GetMapping("/period")
    public ResponseEntity<List<HistoriqueDeclarationUser>> getByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(historiqueService.getHistoriqueByPeriod(startDate, endDate));
    }

   @GetMapping("/declaration/{declarationId}/role/{role}/utilisateur")
    public ResponseEntity<Utilisateur> getFirstUtilisateurByRoleAndDeclaration(
            @PathVariable Long declarationId,
            @PathVariable RoleEnum role) {
        return historiqueService.getFirstUtilisateurByRoleAndDeclaration(role, declarationId)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
    }
}