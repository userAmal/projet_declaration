package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.EtatDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.TypeDeclarationEnum;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDeclarationData extends JpaRepository<Declaration, Long> {
        List<Declaration> findByUtilisateurId(Long utilisateurId);
          
    @Query("SELECT d FROM Declaration d " +
           "LEFT JOIN FETCH d.assujetti " +
           "WHERE d.id = :declarationId")
    Optional<Declaration> getDeclarationWithAssujetti(@Param("declarationId") Long declarationId);
    

    @Query("SELECT DISTINCT d FROM Declaration d " +
           "LEFT JOIN FETCH d.assujetti a " +
           "WHERE d.id = :declarationId")
    Optional<Declaration> getFullDeclarationDetails(@Param("declarationId") Long declarationId);

    @Query("SELECT d FROM Declaration d WHERE " +
    "LOWER(d.assujetti.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    "OR LOWER(d.assujetti.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))")
       List<Declaration> searchByNomOrPrenomAssujetti(@Param("keyword") String keyword);

       @Query("SELECT d FROM Declaration d WHERE d.utilisateur = :utilisateur")
       List<Declaration> findByUtilisateur(@Param("utilisateur") Utilisateur utilisateur);
       boolean existsByUtilisateurId(Long utilisateurId);

       boolean existsByAssujettiIdAndEtatDeclaration(Long assujettiId, EtatDeclarationEnum etatDeclaration);

       List<Declaration> findByEtatDeclarationIn(List<EtatDeclarationEnum> etats);
       boolean existsByUtilisateurIdAndEtatDeclarationNotIn(Long utilisateurId, List<EtatDeclarationEnum> etats);

       @Query("SELECT d FROM Declaration d WHERE d.utilisateur.id = :userId AND d.etatDeclaration IN :etats")
       List<Declaration> findByUtilisateurIdAndEtatDeclarationIn(@Param("userId") Long userId, @Param("etats") List<EtatDeclarationEnum> etats);

       @Query("SELECT COUNT(d) FROM Declaration d WHERE d.utilisateur.id = :userId AND d.etatDeclaration IN :etats")
       long countByUtilisateurIdAndEtatDeclarationIn(@Param("userId") Long userId, @Param("etats") List<EtatDeclarationEnum> etats);

       @Query("SELECT d FROM Declaration d WHERE d.id IN :ids AND d.utilisateur.id = :userId")
       List<Declaration> findByIdInAndUtilisateurId(@Param("ids") List<Long> ids, @Param("userId") Long userId);
 long countByTypeDeclaration(TypeDeclarationEnum type);
    
    @Query("SELECT COUNT(d) FROM Declaration d WHERE d.etatDeclaration = :etat")
    long countByEtatDeclaration(@Param("etat") EtatDeclarationEnum etat);



    @Query(value = "SELECT COUNT(*) FROM declaration", nativeQuery = true)
Long countAllDeclarations();

@Query(value = "SELECT type_declaration, COUNT(*) FROM declaration GROUP BY type_declaration", nativeQuery = true)
List<Object[]> countByType();

@Query(value = "SELECT etat_declaration, COUNT(*) FROM declaration GROUP BY etat_declaration", nativeQuery = true)
List<Object[]> countByEtat();

@Query(value = "SELECT YEAR(date_declaration), COUNT(*) FROM declaration GROUP BY YEAR(date_declaration)", nativeQuery = true)
List<Object[]> countByYear();


@Query("SELECT d FROM Declaration d JOIN HistoriqueDeclarationUser h ON d.id = h.declaration.id " +
       "WHERE h.utilisateur.id = :conseillerId AND h.dateFinAffectation IS NULL")
List<Declaration> findActiveDeclarationsByConseillerId(@Param("conseillerId") Long conseillerId);

@Query("SELECT COUNT(d) FROM Declaration d WHERE d.utilisateur.id = :conseillerId " +
       "AND d.etatDeclaration = :etat")
long countByConseillerIdAndEtat(@Param("conseillerId") Long conseillerId, 
                               @Param("etat") EtatDeclarationEnum etat);

@Query("SELECT DISTINCT d.typeDeclaration FROM Declaration d " +
       "JOIN HistoriqueDeclarationUser h ON d.id = h.declaration.id " +
       "WHERE h.utilisateur.id = :conseillerId")
List<TypeDeclarationEnum> findDistinctTypeDeclarationsByConseiller(@Param("conseillerId") Long conseillerId);
       }
       