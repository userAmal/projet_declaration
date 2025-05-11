package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IFoncierBatiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.PredictionResult;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IFoncierBatiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierBatiProjection;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FoncierBatiService implements IFoncierBatiService {

    private final IFoncierBatiData data;

    public FoncierBatiService(IFoncierBatiData data) {
        this.data = data;
    }

    @Override
    public List<FoncierBatiProjection> getByDeclaration(Long declarationId) {
        return data.findByIdDeclaration_Id(declarationId);
    }

    @Override
    public List<FoncierBati> findAll() {
        return data.findAll();
    }

    @Override
    public Optional<FoncierBati> findById(Long id) {
        return data.findById(id);
    }

    @Override
    public FoncierBati save(FoncierBati entity) {
        return data.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        data.deleteById(id);
    }
    @Override
    public List<FoncierBati> getFullEntitiesByDeclaration(Long declarationId) {
        return data.findByIdDeclaration_Id(declarationId)
                .stream()
                .map(p -> data.findById(p.getId()).orElse(null)) // ou une vraie requête jointe pour optimiser
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public double getPrediction(FoncierBati foncierBati) {
        RestTemplate restTemplate = new RestTemplate();
    
        // URL de l’endpoint Flask (POST avec entité "foncierbati")
        String url = "http://localhost:5000/predict/foncierbati";
    
        // Préparer les données sous forme de JSON (Map)
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("nature", foncierBati.getNature().getIntitule());
    
        // Prétraiter la superficie pour enlever les unités (ex: "500m²" -> "500")
        String superficie = foncierBati.getSuperficie();
        if (superficie != null) {
            superficie = superficie.replaceAll("[^0-9]", ""); // Enlever les caractères non numériques
        }
        requestData.put("superficie", superficie);
    
        requestData.put("anneeConstruction", foncierBati.getAnneeConstruction());
        requestData.put("localisation", foncierBati.getLocalis().getIntitule());
    
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

 public byte[] generatePdfRapport(List<PredictionResult> results) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    PdfWriter writer = new PdfWriter(out);
    PdfDocument pdfDoc = new PdfDocument(writer);
    Document document = new Document(pdfDoc);

    float[] columnWidths = {1, 1, 1, 1, 1}; // 5 colonnes égales
    Table table = new Table(columnWidths);

    table.addHeaderCell("Nature");
    table.addHeaderCell("Superficie");
    table.addHeaderCell("Année construction");
    table.addHeaderCell("Valeur déclarée");
    table.addHeaderCell("Prédiction modèle");

    for (PredictionResult result : results) {
        FoncierBati f = result.getFoncierBati();
        double prediction = result.getPrediction();
        double declaredValue = f.getCoutAcquisitionFCFA();

        Color color = Math.abs(declaredValue - prediction) > 1_000_000
                ? new DeviceRgb(255, 0, 0)   // Rouge
                : new DeviceRgb(0, 255, 0);  // Vert

        table.addCell(new Cell().add(new Paragraph(f.getNature().getIntitule())));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(f.getSuperficie()))));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(f.getAnneeConstruction()))));
        table.addCell(createColoredCell(String.valueOf(declaredValue), color));
        table.addCell(createColoredCell(String.format("%.2f", prediction), color));
    }

    document.add(table);
    document.close();

    return out.toByteArray();
}

private Cell createColoredCell(String text, Color backgroundColor) {
    return new Cell()
            .add(new Paragraph(text))
            .setBackgroundColor(backgroundColor);
}

    
}
