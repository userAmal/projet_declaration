package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IVehiculeData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vehicule;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IVehiculeService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VehiculeProjection;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;


import org.springframework.http.*;

import java.util.Map;

@Service
public class VehiculeService implements IVehiculeService {

    private final IVehiculeData data;

    public VehiculeService(IVehiculeData data) {
        this.data = data;
    }

    @Override
    public List<VehiculeProjection> getByDeclaration(Long declarationId) {
        return data.findByIdDeclaration_Id(declarationId);
    }

    @Override
    public List<Vehicule> findAll() {
        return data.findAll();
    }

    @Override
    public Optional<Vehicule> findById(Long id) {
        return data.findById(id);
    }

    @Override
    public Vehicule save(Vehicule entity) {
        return data.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        data.deleteById(id);
    }

    // Nouvelle méthode POST pour obtenir la prédiction du prix via l'API Flask pour un véhicule
    public double getPrediction(Vehicule vehicule) {
        RestTemplate restTemplate = new RestTemplate();

        // URL de l’endpoint Flask (POST avec entité "vehicule")
        String url = "http://localhost:5000/predict/vehicule";

        // Préparer les données sous forme de JSON (Map)
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("marque", vehicule.getMarque());
        requestData.put("etatGeneral", vehicule.getEtatGeneral());
        requestData.put("kilometrage", vehicule.getKilometrage());
        requestData.put("anneeAcquisition", vehicule.getAnneeAcquisition());

        // Créer les headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Créer l’objet HttpEntity (corps + headers)
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);

        // Faire l’appel POST
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        // Récupérer la prédiction
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("prediction")) {
                return Double.parseDouble(body.get("prediction").toString());
            }
        }

        // En cas d’échec
        throw new RuntimeException("Erreur lors de la prédiction avec l’API Flask.");
    }

}
