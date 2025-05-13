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
public class AppareilsElectroMenagers {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire designation;

    @Column(nullable = false)
    private int anneeAcquisition;

    @Column(nullable = false)
    private float valeurAcquisition;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire etatGeneral;

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
