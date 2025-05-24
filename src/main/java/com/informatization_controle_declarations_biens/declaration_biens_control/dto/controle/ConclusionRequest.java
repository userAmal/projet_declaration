package com.informatization_controle_declarations_biens.declaration_biens_control.dto.controle;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.securite.Utilisateur;

public class ConclusionRequest {

    private Declaration declaration;
    private byte[] pdfContent;
    private String fileName;
    private Utilisateur utilisateur; // Ajoutez l'utilisateur ici
    private String contenuUtilisateur; // Ajoutez le contenu utilisateur ici
    private boolean estAcceptation;
    // Getters et setters

    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    public byte[] getPdfContent() {
        return pdfContent;
    }

    public void setPdfContent(byte[] pdfContent) {
        this.pdfContent = pdfContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getContenuUtilisateur() {
        return contenuUtilisateur;
    }

    public void setContenuUtilisateur(String contenuUtilisateur) {
        this.contenuUtilisateur = contenuUtilisateur;
    }

     public boolean isEstAcceptation() {
        return estAcceptation;
    }

    public void setEstAcceptation(boolean estAcceptation) {
        this.estAcceptation = estAcceptation;
    }
}
