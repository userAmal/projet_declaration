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
import com.itextpdf.layout.borders.SolidBorder;
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
    Document document = new Document(pdfDoc, PageSize.A4.rotate()); // Format paysage
    
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

    // Configuration du tableau avec colonnes ajustées
    float[] columnWidths = {1.8f, 1.2f, 1.3f, 1.4f, 1.3f, 1.5f, 1.5f, 1.8f}; // Répartition optimisée
    Table table = new Table(UnitValue.createPercentArray(columnWidths));
    table.setWidth(UnitValue.createPercentValue(100));
    table.setKeepTogether(false);
    table.setFixedLayout(); // Force le respect des largeurs définies
    
    // Style des en-têtes
    Color headerColor = new DeviceRgb(31, 73, 125);
    Color headerTextColor = DeviceRgb.WHITE;
    float fontSize = 9; // Taille réduite pour plus d'espace

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

    // Légende des couleurs
    addColorLegend(document);
    
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

// Méthodes utilitaires modifiées pour un affichage optimisé
private Cell createHeaderCell(String text, Color bgColor, Color textColor, float fontSize) {
    return new Cell()
            .add(new Paragraph(text)
                .setFontColor(textColor)
                .setFontSize(fontSize)
                .setBold()
                .setMultipliedLeading(1.0f))
            .setBackgroundColor(bgColor)
            .setPadding(5) // Padding réduit
            .setTextAlignment(TextAlignment.CENTER)
            .setHeight(25) // Hauteur fixe
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
}

private Cell createContentCell(String content, float fontSize) {
    return new Cell()
            .add(new Paragraph(content)
                .setFontSize(fontSize)
                .setMultipliedLeading(1.0f))
            .setPadding(3) // Padding réduit
            .setTextAlignment(TextAlignment.CENTER)
            .setHeight(20) // Hauteur fixe
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
}

private Cell createFinancialCell(double value, DecimalFormat df, float fontSize) {
    return new Cell()
            .add(new Paragraph(formatCurrencyShort(value, df))
                .setFontSize(fontSize)
                .setMultipliedLeading(1.0f))
            .setPadding(3)
            .setTextAlignment(TextAlignment.RIGHT)
            .setHeight(20)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
}

private Cell createPredictionCell(double prediction, double ecartPourcentage, 
                                DecimalFormat df, float fontSize) {
    Color bgColor = getEcartColor(ecartPourcentage);
    
    // Format compact sur une ligne
    String text = String.format("%s (Écart: %.1f%%)", 
                  formatCurrencyShort(prediction, df), 
                  ecartPourcentage);
    
    return new Cell()
            .add(new Paragraph(text)
                .setFontSize(fontSize - 0.5f) // Police légèrement plus petite
                .setMultipliedLeading(1.0f))
            .setBackgroundColor(bgColor)
            .setPadding(3)
            .setTextAlignment(TextAlignment.RIGHT)
            .setHeight(20)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
}

// Méthode pour ajouter la légende des couleurs (version compacte)
private void addColorLegend(Document document) {
    // Titre de la légende plus petit
    Paragraph legendTitle = new Paragraph("LÉGENDE DES COULEURS")
            .setTextAlignment(TextAlignment.LEFT)
            .setFontSize(9)
            .setBold()
            .setMarginTop(15)
            .setMarginBottom(5);
    document.add(legendTitle);

    // Tableau pour la légende plus compact
    float[] legendWidths = {0.3f, 2f};
    Table legendTable = new Table(UnitValue.createPercentArray(legendWidths));
    legendTable.setWidth(UnitValue.createPercentValue(35));
    legendTable.setMarginBottom(8);

    // Couleur verte - Écart acceptable
    Color greenColor = new DeviceRgb(204, 255, 204);
    legendTable.addCell(createLegendColorCell(greenColor));
    legendTable.addCell(createLegendTextCell("Écart ≤ 10% - Acceptable"));

    // Couleur orange - Écart modéré
    Color orangeColor = new DeviceRgb(255, 204, 153);
    legendTable.addCell(createLegendColorCell(orangeColor));
    legendTable.addCell(createLegendTextCell("Écart 10-20% - Attention"));

    // Couleur rouge - Écart élevé
    Color redColor = new DeviceRgb(255, 153, 153);
    legendTable.addCell(createLegendColorCell(redColor));
    legendTable.addCell(createLegendTextCell("Écart > 20% - Contrôle"));

    document.add(legendTable);

    // Note explicative plus courte
    Paragraph note = new Paragraph("Note: L'écart représente la différence absolue entre la valeur déclarée et la prédiction du modèle, exprimée en pourcentage de la valeur déclarée.")
            .setFontSize(7)
            .setItalic()
            .setMarginTop(3)
            .setTextAlignment(TextAlignment.LEFT);
    document.add(note);
}

// Méthodes utilitaires pour la légende (version compacte)
private Cell createLegendColorCell(Color color) {
    return new Cell()
            .setBackgroundColor(color)
            .setHeight(12)
            .setBorder(new SolidBorder(0.5f))
            .setPadding(2);
}

private Cell createLegendTextCell(String text) {
    return new Cell()
            .add(new Paragraph(text).setFontSize(8))
            .setPadding(2)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setBorder(new SolidBorder(0.5f));
}

private String cleanSuperficie(String superficie) {
    return superficie.replaceAll("[^0-9.]", "") + " m²";
}

private String formatCurrency(double amount, DecimalFormat df) {
    return df.format(amount).replace(",", " ") + " FCFA";
}

// Version courte pour format monétaire
private String formatCurrencyShort(double amount, DecimalFormat df) {
    String formatted = df.format(amount).replace(",", " ");
    // Abréviation pour économiser l'espace si le montant est très grand
    if (amount >= 1000000) {
        return new DecimalFormat("#,##0.0").format(amount / 1000000).replace(",", " ") + "M FCFA";
    }
    return formatted + " FCFA";
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
