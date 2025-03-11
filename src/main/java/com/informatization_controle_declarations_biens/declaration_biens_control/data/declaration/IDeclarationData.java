package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;


import org.springframework.data.jpa.repository.JpaRepository;


public interface IDeclarationData extends JpaRepository<Declaration, Long> {

}