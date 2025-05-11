package com.informatization_controle_declarations_biens.declaration_biens_control.data.controle;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IRapportData extends JpaRepository<Rapport, Long> {
    List<Rapport> findByDeclarationId(Long declarationId);
    List<Rapport> findByUtilisateurId(Long utilisateurId);
    List<Rapport> findByType(Rapport.Type type);
}