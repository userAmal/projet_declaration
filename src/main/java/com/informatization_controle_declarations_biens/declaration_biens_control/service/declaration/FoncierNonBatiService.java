package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IFoncierNonBatiData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.PredictionResult;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierNonBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IFoncierNonBatiService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierNonBatiProjection;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.properties.UnitValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.time.LocalDate;

import com.itextpdf.layout.element.Table;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class FoncierNonBatiService implements IFoncierNonBatiService {

    @Autowired
    private IFoncierNonBatiData Data;

    @Override
    public List<FoncierNonBati> findAll() {
        return Data.findAll();
    }
    @Override
    public List<FoncierNonBati> findByNatureId(Long natureId) {
        return Data.findByNatureId(natureId);
    }
    @Override
    public Optional<FoncierNonBati> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Data.findSimplifiedById(id);    }

    @Override
    public FoncierNonBati save(FoncierNonBati foncierNonBati) {
        return Data.save(foncierNonBati);
    }

    @Override
    public List<FoncierNonBati> getFullEntitiesByDeclaration(Long declarationId) {
        // Get projections first
        List<FoncierNonBatiProjection> projections = Data.findByDeclarationId(declarationId);
        
        // Convert to full entities
        return projections.stream()
                .map(proj -> Data.findById(proj.getId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        Data.deleteById(id);
    }

    @Override
    public List<FoncierNonBatiProjection> getByDeclaration(Long declarationId) {
        return Data.findByDeclarationId(declarationId);
    }

    @Override
    public double getPrediction(FoncierNonBati foncierNonBati) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:5000/predict/fonciernonbati";

        // Conversion robuste de la superficie
        double superficie;
        try {
            superficie = Double.parseDouble(foncierNonBati.getSuperficie().replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            superficie = 0;
        }

        // Construction du payload spécifique au modèle non bâti
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("lotissement", foncierNonBati.getLotissement());
        requestData.put("superficie", superficie);
        requestData.put("localite", foncierNonBati.getLocalite());
        requestData.put("type_terrain", foncierNonBati.getTypeTerrain().getIntitule());
        requestData.put("coutInvestissements", foncierNonBati.getCoutInvestissements());

        // Debug
        System.out.println("Requête envoyée à Flask (Non Bâti): " + requestData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            System.out.println("Réponse reçue de Flask (Non Bâti): " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();
                if (body != null && body.containsKey("prediction")) {
                    Object prediction = body.get("prediction");
                    if (prediction instanceof Number) {
                        return ((Number) prediction).doubleValue();
                    } else {
                        return Double.parseDouble(prediction.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Erreur lors de la prédiction avec l'API Flask pour le foncier non bâti.");
    }

    @Override
    public byte[] generatePdfRapportNonBati(List<PredictionResult> results) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4.rotate());
        
        // Titre principal
        Paragraph title = new Paragraph("RAPPORT DE CONTRÔLE - FONCIER NON BÂTI")
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

        // Configuration du tableau (8 colonnes)
        float[] columnWidths = {2f, 1.5f, 1.5f, 1.5f, 1.5f, 2f, 2f, 2f};
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Style des en-têtes
        Color headerColor = new DeviceRgb(31, 73, 125);
        Color headerTextColor = DeviceRgb.WHITE;
        float fontSize = 9;

        // En-têtes du tableau spécifiques au non bâti
        String[] headers = {
            "Lotissement", "Superficie (m²)", "Localité", 
            "Type de terrain", "Investissements", "Réf. Cadastrales", 
            "Valeur déclarée", "Prédiction modèle"
        };
        
        for (String header : headers) {
            table.addHeaderCell(createHeaderCell(header, headerColor, headerTextColor, fontSize));
        }

        // Formatage des nombres
        DecimalFormat df = new DecimalFormat("#,##0.00");
        
        // Remplissage des données
        for (PredictionResult result : results) {
            FoncierNonBati f = result.getFoncierNonBati();
            double prediction = result.getPrediction();
            double declaredValue = f.getValeurAcquisFCFA();
            double ecartPourcentage = calculateEcartPercentage(declaredValue, prediction);

            // Ligne du tableau
            table.addCell(createContentCell(f.getLotissement(), fontSize));
            table.addCell(createContentCell(cleanSuperficie(f.getSuperficie()), fontSize));
            table.addCell(createContentCell(f.getLocalite(), fontSize));
            table.addCell(createContentCell(f.getTypeTerrain().getIntitule(), fontSize));
            table.addCell(createFinancialCell(f.getCoutInvestissements(), df, fontSize));
            table.addCell(createContentCell(f.getTitrePropriete(), fontSize));
            table.addCell(createFinancialCell(declaredValue, df, fontSize));
            table.addCell(createPredictionCell(prediction, ecartPourcentage, df, fontSize));
        }

        document.add(table);
        
        // Pied de page
        Paragraph footer = new Paragraph("Généré le " + LocalDate.now() + " | Système de contrôle des déclarations - Foncier Non Bâti")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10)
                .setItalic()
                .setMarginTop(20);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    // Méthodes utilitaires (identique à FoncierBatiService mais adapté si nécessaire)
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

    private String cleanSuperficie(String superficie) {
        return superficie.replaceAll("[^0-9.]", "") + " m²";
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
