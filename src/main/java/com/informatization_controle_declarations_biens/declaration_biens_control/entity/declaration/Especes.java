package com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Especes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private float monnaie;

    @Column(nullable = false)
    private float devise;

    @Column(nullable = false)
    private float tauxChange;

    @Column(nullable = false)
    private float montantFCFA;

    @Column(nullable = false)
    private float montantTotalFCFA;

    @Column(nullable = false)
    private LocalDate dateEspece;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @Column(nullable = false)
    private boolean isSynthese;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Declaration idDeclaration;

    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Lob
        @JsonIgnore 
    @Column(name = "file_data")
    private byte[] fileData;

}
