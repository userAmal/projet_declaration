package com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.MagicToken;

public interface MagicTokenRepository extends JpaRepository<MagicToken, Long> {
    Optional<MagicToken> findByToken(String token);
}
