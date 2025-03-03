package com.informatization_controle_declarations_biens.declaration_biens_control.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Custom password encoder that handles both BCrypt and plain text passwords
 * This is a temporary solution to handle the transition from plain text to encoded passwords
 */
public class CustomPasswordEncoder implements PasswordEncoder {
    
    private final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
    
    @Override
    public String encode(CharSequence rawPassword) {
        // Always encode using BCrypt
        return bCryptEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Check if the password is BCrypt encoded (starts with "$2a$")
        if (encodedPassword.startsWith("$2a$")) {
            // Try BCrypt matching
            return bCryptEncoder.matches(rawPassword, encodedPassword);
        } else {
            // If not BCrypt encoded, assume it's stored in plain text
            // and do a direct string comparison
            return rawPassword.toString().equals(encodedPassword);
        }
    }
}