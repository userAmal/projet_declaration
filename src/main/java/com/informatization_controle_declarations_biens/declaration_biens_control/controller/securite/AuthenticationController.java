package com.informatization_controle_declarations_biens.declaration_biens_control.controller.securite;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.AuthenticationService;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.securite.JWTService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JWTService jwtService;

    // @PostMapping("/register")
    // public ResponseEntity<AuthenticationResponse> register(
    //         @RequestBody RegisterRequest request) {
    //     return ResponseEntity.ok(authenticationService.register(request));
    // }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/logout")
public ResponseEntity<Object> logout(@RequestHeader("Authorization") String token) {
    String jwt = token.substring(7);  // Remove "Bearer " prefix
    
    jwtService.invalidateToken(jwt);
    
    return ResponseEntity.ok().body(Map.of("message", "Logout successful"));
}


}