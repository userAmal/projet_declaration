package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IFoncierNonBatiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierNonBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IFoncierNonBatiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierNonBatiProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FoncierNonBatiService implements IFoncierNonBatiService {

    @Autowired
    private IFoncierNonBatiData Data;

    @Override
    public List<FoncierNonBati> findAll() {
        return Data.findAll();
    }

    @Override
    public Optional<FoncierNonBati> findById(Long id) {
        return Data.findById(id);
    }

    @Override
    public FoncierNonBati save(FoncierNonBati foncierNonBati) {
        return Data.save(foncierNonBati);
    }

    @Override
    public void deleteById(Long id) {
        Data.deleteById(id);
    }

    @Override
    public List<FoncierNonBatiProjection> getByDeclaration(Long declarationId) {
        return Data.findByDeclarationId(declarationId);
    }

    public double getPrediction(FoncierNonBati foncierNonBati) {
        RestTemplate restTemplate = new RestTemplate();
    
        // URL de l’API Flask pour Foncier Non Bâti
        String url = "http://localhost:5000/predict/fonciernonbati";
    
        // Préparer les données JSON attendues par Flask
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("lotissement", foncierNonBati.getLotissement()); // ex: "ZAC Nord"
        requestData.put("superficie", foncierNonBati.getSuperficie());   // ex: 500
        requestData.put("localite", foncierNonBati.getLocalite());       // ex: "Abidjan"
        requestData.put("dateAcquis", foncierNonBati.getDateAcquis());   // ex: 2010
        requestData.put("coutInvestissements", foncierNonBati.getCoutInvestissements()); // ex: 1500000
    
        // Headers de la requête
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        // Corps de la requête
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);
    
        // Envoi de la requête POST
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
    
        // Lecture de la prédiction
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("prediction")) {
                return Double.parseDouble(body.get("prediction").toString());
            }
        }
    
        // En cas d’erreur
        throw new RuntimeException("Erreur lors de la prédiction avec l’API Flask (foncier non bâti).");
    }
}
