package com.informatization_controle_declarations_biens.declaration_biens_control.controller.control;


import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class PdfFillingService {

    @Value("${app.pdf.template.path:templates/modele_declaration.pdf}")
    private String templatePath;

    @Value("${app.pdf.output.dir:generated_pdfs/}")
    private String outputDir;

    /**
     * Remplit le modèle PDF avec des données fournies et retourne le chemin du fichier généré.
     * @param data Données à insérer (ex. nom, date, type déclaration, etc.)
     * @param filename Nom du fichier PDF généré
     * @return Chemin du fichier PDF généré
     */
    public String fillPdfWithData(Map<String, String> data, String filename) {
        String outputPath = outputDir + filename;

        try {
            // Copier le modèle existant
            PdfReader reader = new PdfReader(templatePath);
            PdfWriter writer = new PdfWriter(new FileOutputStream(outputPath));
            PdfDocument pdfDoc = new PdfDocument(reader, writer);
            Document document = new Document(pdfDoc);

            // Exemple : Ajout de données en haut du document
            document.add(new Paragraph("\n\n"));

            if (data.containsKey("date")) {
                document.add(new Paragraph("Date de Déclaration : " + data.get("date")));
            }
            if (data.containsKey("type")) {
                document.add(new Paragraph("Type de Déclaration : " + data.get("type")));
            }

            // Tu peux adapter ici pour insérer les données dans les bonnes positions du PDF
            // Pour plus de contrôle (écriture à des coordonnées précises), tu peux utiliser PdfCanvas

            document.close();
            return outputPath;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du remplissage du PDF", e);
        }
    }
}

