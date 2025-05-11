package com.informatization_controle_declarations_biens.declaration_biens_control.data.controle;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.Conclusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IConclusionData extends JpaRepository<Conclusion, Long> {

    @Query("SELECT c FROM Conclusion c WHERE c.declaration.id = :declarationId ORDER BY c.dateCreation DESC")
    List<Conclusion> findByDeclarationId(@Param("declarationId") Long declarationId);

    @Query("SELECT c FROM Conclusion c WHERE c.utilisateur.id = :utilisateurId ORDER BY c.dateCreation DESC")
    List<Conclusion> findByUtilisateurId(@Param("utilisateurId") Long utilisateurId);
    List<Conclusion> findByUtilisateurIdAndDeclarationId(Long utilisateurId, Long declarationId);

}