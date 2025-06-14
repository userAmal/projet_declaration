package com.informatization_controle_declarations_biens.declaration_biens_control.dto.bi.avocatStat;

import java.util.List;

public  class DeclarationsAnciennesDTO {
    private List<DeclarationAncienneInfo> declarations;

    public DeclarationsAnciennesDTO(List<DeclarationAncienneInfo> declarations) {
        this.declarations = declarations;
    }

    // Getter
    public List<DeclarationAncienneInfo> getDeclarations() { return declarations; }
}
