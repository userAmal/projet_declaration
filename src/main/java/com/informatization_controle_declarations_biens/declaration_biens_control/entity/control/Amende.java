package com.informatization_controle_declarations_biens.declaration_biens_control.entity.control;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "amendes")
public class Amende implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "declaration_id")
    private Declaration declaration;
    
    @Column(name = "date_amende")
    private LocalDate dateAmende;
    
    @Column(name = "montant", precision = 10, scale = 2)
    private BigDecimal montant;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutAmendeEnum statut;
    
    @Column(name = "motif", length = 255)
    private String motif;
    
    @Column(name = "date_paiement")
    private LocalDate datePaiement;
    
    @Column(name = "reference_paiement", length = 50)
    private String referencePaiement;
}
