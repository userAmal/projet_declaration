package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TypeVocabulaire {



    public TypeVocabulaire() {
    }

    public TypeVocabulaire(Long id, String intitule) {
        this.id = id;

        this.intitule = intitule;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 100, nullable = false)
    private String intitule;
}
