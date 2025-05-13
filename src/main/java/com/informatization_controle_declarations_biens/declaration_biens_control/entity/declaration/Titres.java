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
public class Titres {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire designationNatureActions;

    private float valeurEmplacement;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire emplacement;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire autrePrecisions;

    private LocalDate dateCreation;
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
