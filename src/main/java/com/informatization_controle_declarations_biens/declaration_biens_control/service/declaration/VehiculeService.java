package com.informatization_controle_declarations_biens.declaration_biens_control.service.declaration;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.declaration.IVehiculeData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.control.PredictionResult;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierNonBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vehicule;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IVehiculeService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.FoncierNonBatiProjection;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.VehiculeProjection;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.UnitValue;

import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.colors.Color;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.io.ByteArrayOutputStream;
import com.itextpdf.layout.properties.TextAlignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import com.itextpdf.layout.element.Cell;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehiculeService implements IVehiculeService {

    private final IVehiculeData data;

    @Autowired
    public VehiculeService(IVehiculeData data ) {
        this.data = data;

    }
    @Override
    public List<Vehicule> findAllById(List<Long> ids) {
        return data.findAllById(ids);
    }


    @Override
    public List<Vehicule> getFullEntitiesByDeclaration(Long declarationId) {
        return data.findByIdDeclaration_Id(declarationId)
                .stream()
                .map(projection -> findById(projection.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
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
    Document document = new Document(pdfDoc, PageSize.A4.rotate()); // Format paysage pour plus d'espace

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

    // Configuration du tableau avec colonnes ajustées
    float[] columnWidths = {1.5f, 1.8f, 1f, 1.2f, 1.2f, 1.3f, 1.5f, 1.8f}; // Répartition plus équilibrée
    Table table = new Table(UnitValue.createPercentArray(columnWidths));
    table.setWidth(UnitValue.createPercentValue(100));
    table.setKeepTogether(false); // Permet de casser le tableau sur plusieurs pages si nécessaire
    table.setFixedLayout(); // Force le respect des largeurs définies

    // Style des en-têtes
    Color headerColor = new DeviceRgb(31, 73, 125);
    Color headerTextColor = DeviceRgb.WHITE;
    float fontSize = 9; // Taille de police réduite pour plus d'espace

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

    // Légende des couleurs
    addColorLegend(document);

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

// Méthodes utilitaires modifiées
private Cell createHeaderCell(String text, Color bgColor, Color textColor, float fontSize) {
    return new Cell()
            .add(new Paragraph(text)
                .setFontColor(textColor)
                .setFontSize(fontSize)
                .setBold()
                .setMultipliedLeading(1.0f)) // Contrôle l'espacement des lignes
            .setBackgroundColor(bgColor)
            .setPadding(5) // Padding réduit
            .setTextAlignment(TextAlignment.CENTER)
            .setHeight(25) // Hauteur fixe pour uniformité
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
            .add(new Paragraph(formatCurrency(value, df))
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
    
    // Formatage sur une seule ligne avec écart en parenthèses
    String text = String.format("%s (Écart: %.1f%%)", 
                  formatCurrencyShort(prediction, df), // Version courte pour économiser l'espace
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

private String formatKilometrage(double km) {
    // Version plus courte
    return new DecimalFormat("#,##0").format(km).replace(",", " ") + "km";
}

private String formatCurrency(double amount, DecimalFormat df) {
    return df.format(amount).replace(",", " ") + " FCFA";
}

// Nouvelle méthode pour format monétaire court
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

// Méthode pour ajouter la légende des couleurs (version compacte)
private void addColorLegend(Document document) {
    // Titre de la légende plus petit
    Paragraph legendTitle = new Paragraph("LÉGENDE DES COULEURS")
            .setTextAlignment(TextAlignment.LEFT)
            .setFontSize(9) // Réduit de 12 à 9
            .setBold()
            .setMarginTop(15) // Réduit de 20 à 15
            .setMarginBottom(5); // Réduit de 10 à 5
    document.add(legendTitle);

    // Tableau pour la légende plus compact
    float[] legendWidths = {0.3f, 2f}; // Cellules de couleur plus petites
    Table legendTable = new Table(UnitValue.createPercentArray(legendWidths));
    legendTable.setWidth(UnitValue.createPercentValue(35)); // Réduit de 50% à 35%
    legendTable.setMarginBottom(8); // Réduit de 15 à 8

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

    // Note explicative plus courte et plus petite
    Paragraph note = new Paragraph("Note: L'écart représente la différence absolue entre la valeur déclarée et la prédiction du modèle, exprimée en pourcentage de la valeur déclarée.")
            .setFontSize(7) // Réduit de 9 à 7
            .setItalic()
            .setMarginTop(3) // Réduit de 5 à 3
            .setTextAlignment(TextAlignment.LEFT); // Changé de JUSTIFIED à LEFT
    document.add(note);
}

// Méthodes utilitaires pour la légende (version compacte)
private Cell createLegendColorCell(Color color) {
    return new Cell()
            .setBackgroundColor(color)
            .setHeight(12) // Réduit de 20 à 12
            .setBorder(new SolidBorder(0.5f)) // Bordure plus fine
            .setPadding(2); // Réduit de 5 à 2
}

private Cell createLegendTextCell(String text) {
    return new Cell()
            .add(new Paragraph(text).setFontSize(8)) // Réduit de 10 à 8
            .setPadding(2) // Réduit de 5 à 2
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setBorder(new SolidBorder(0.5f)); // Bordure plus fine
}
}
