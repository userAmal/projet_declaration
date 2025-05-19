package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IVehiculeData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.PredictionResult;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vehicule;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IVehiculeService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VehiculeProjection;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.UnitValue;

import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.colors.Color;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import java.io.ByteArrayOutputStream;
import com.itextpdf.layout.properties.TextAlignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import java.util.Map;


import com.itextpdf.layout.element.Cell;

import java.util.*;

@Service
public class VehiculeService implements IVehiculeService {

    private final IVehiculeData data;

    @Autowired
    public VehiculeService(IVehiculeData data ) {
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
        if (id == null) {
            return Optional.empty();
        }
        return data.findSimplifiedById(id);    }

    @Override
    public Vehicule save(Vehicule entity) {
        return data.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        data.deleteById(id);
    }

    @Override
    public List<Vehicule> findByDesignation(Long designationId) {
    return data.findByDesignationId(designationId);

    }
    @Override
    public double getPrediction(Vehicule vehicule) {
        // Création locale du RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            // Préparer les données selon ce que le modèle attend
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("Year", vehicule.getAnneeAcquisition());
            requestData.put("Present_Price", vehicule.getValeurAcquisition());
            requestData.put("Kms_Driven", vehicule.getKilometrage());
            requestData.put("Fuel_Type", vehicule.getCarburant().getIntitule());
            requestData.put("Transmission", vehicule.getTransmission().getIntitule());

            // Debug: Afficher la requête
            System.out.println("Requête envoyée à Flask: " + requestData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:5000/predict/car_price", 
                requestEntity, 
                Map.class
            );

            System.out.println("Réponse reçue de Flask: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object prediction = response.getBody().get("prediction");
                if (prediction instanceof Number) {
                    return ((Number) prediction).doubleValue();
                } else {
                    return Double.parseDouble(prediction.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la prédiction: " + e.getMessage());
        }
        throw new RuntimeException("Erreur lors de la prédiction avec l'API Flask.");
    }

    @Override
public byte[] generatePdfRapport(List<PredictionResult> results) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PdfWriter writer = new PdfWriter(out);
    PdfDocument pdfDoc = new PdfDocument(writer);
    Document document = new Document(pdfDoc);

    // Titre principal
    Paragraph title = new Paragraph("RAPPORT DE CONTRÔLE DES VÉHICULES")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(16)
            .setBold()
            .setMarginBottom(15);
    document.add(title);

    // Sous-titre
    Paragraph subtitle = new Paragraph("Analyse des écarts entre valeurs déclarées et prédictions du modèle")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(12)
            .setMarginBottom(20);
    document.add(subtitle);

    // Configuration du tableau
    float[] columnWidths = {2f, 2f, 1.5f, 1.5f, 1.5f, 2f, 2f};
    Table table = new Table(UnitValue.createPercentArray(columnWidths));
    table.setWidth(UnitValue.createPercentValue(100));

    // Style des en-têtes
    Color headerColor = new DeviceRgb(31, 73, 125);
    Color headerTextColor = DeviceRgb.WHITE;
    float fontSize = 10;

    // En-têtes du tableau
    String[] headers = {
        "Marque", "Immatriculation", "Année", 
        "Kilométrage", "Carburant", "Transmission",
        "Valeur déclarée", "Prédiction modèle"
    };

    for (String header : headers) {
        table.addHeaderCell(createHeaderCell(header, headerColor, headerTextColor, fontSize));
    }

    // Formatage des nombres
    DecimalFormat df = new DecimalFormat("#,##0.00");

    // Remplissage des données
    for (PredictionResult result : results) {
        Vehicule v = result.getVehicule();
        double prediction = result.getPrediction();
        double declaredValue = v.getValeurAcquisition();
        double ecartPourcentage = calculateEcartPercentage(declaredValue, prediction);

        table.addCell(createContentCell(v.getMarque().getIntitule(), fontSize));
        table.addCell(createContentCell(v.getImmatriculation(), fontSize));
        table.addCell(createContentCell(String.valueOf(v.getAnneeAcquisition()), fontSize));
        table.addCell(createContentCell(formatKilometrage(v.getKilometrage()), fontSize));
        table.addCell(createContentCell(v.getCarburant().getIntitule(), fontSize));
        table.addCell(createContentCell(v.getTransmission().getIntitule(), fontSize));
        table.addCell(createFinancialCell(declaredValue, df, fontSize));
        table.addCell(createPredictionCell(prediction, ecartPourcentage, df, fontSize));
    }

    document.add(table);

    // Pied de page
    Paragraph footer = new Paragraph("Généré le " + LocalDate.now() + " | Système de contrôle des déclarations")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(10)
            .setItalic()
            .setMarginTop(20);
    document.add(footer);

    document.close();
    return out.toByteArray();
}

    // Méthodes utilitaires
    private Cell createHeaderCell(String text, Color bgColor, Color textColor, float fontSize) {
        return new Cell()
                .add(new Paragraph(text)
                    .setFontColor(textColor)
                    .setFontSize(fontSize)
                    .setBold())
                .setBackgroundColor(bgColor)
                .setPadding(7)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createContentCell(String content, float fontSize) {
        return new Cell()
                .add(new Paragraph(content).setFontSize(fontSize))
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createFinancialCell(double value, DecimalFormat df, float fontSize) {
        return new Cell()
                .add(new Paragraph(formatCurrency(value, df)).setFontSize(fontSize))
                .setPadding(5)
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private Cell createPredictionCell(double prediction, double ecartPourcentage, 
                                    DecimalFormat df, float fontSize) {
        Color bgColor = getEcartColor(ecartPourcentage);
        String text = String.format("%s\n(Écart: %.1f%%)", 
                      formatCurrency(prediction, df), 
                      ecartPourcentage);

        return new Cell()
                .add(new Paragraph(text).setFontSize(fontSize))
                .setBackgroundColor(bgColor)
                .setPadding(5)
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private String formatKilometrage(double km) {
        return new DecimalFormat("#,##0").format(km) + " km";
    }

    private String formatCurrency(double amount, DecimalFormat df) {
        return df.format(amount).replace(",", " ") + " FCFA";
    }

    private double calculateEcartPercentage(double declared, double predicted) {
        return (Math.abs(declared - predicted) / declared) * 100;
    }

    private Color getEcartColor(double ecartPourcentage) {
        if (ecartPourcentage > 20) return new DeviceRgb(255, 153, 153);
        if (ecartPourcentage > 10) return new DeviceRgb(255, 204, 153);
        return new DeviceRgb(204, 255, 204);
    }
}
