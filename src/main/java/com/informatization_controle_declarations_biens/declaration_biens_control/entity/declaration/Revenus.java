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
public class Revenus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private float salaireMensuelNet;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Vocabulaire autresRevenus;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dateCreation;

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
