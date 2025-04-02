package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import java.util.List;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.*;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.declaration.*;

@Service
public class PdfFileService {

public void generateFullDeclarationPdf(DeclarationDto dto, String filePath) {
    try (PdfWriter writer = new PdfWriter(filePath);
         PdfDocument pdfDoc = new PdfDocument(writer);
         Document document = new Document(pdfDoc, PageSize.A4.rotate())) {  
        
        document.setMargins(40, 40, 40, 40); 
    
            pdfDoc.addNewPage();
            addMainHeader(document, dto);
            addAssujettiSection(document, dto.getAssujetti());
            
            addVehiculesSection(document, dto.getVehicules());
            addRevenusSection(document, dto.getRevenus());
            addFoncierBatiSection(document, dto.getFonciersBatis());
            addFoncierNonBatiSection(document, dto.getFonciersNonBatis());
            addEmpruntsSection(document, dto.getEmprunts());
            addDisponibilitesBanqueSection(document, dto.getDisponibilitesBanque());
            addEspecesSection(document, dto.getEspeces());
            addAnimauxSection(document, dto.getAnimaux());
            addMeublesSection(document, dto.getMeublesMeublants());
            addTitresSection(document, dto.getTitres());
            addCreancesSection(document, dto.getCreances());
            addAutresDettesSection(document, dto.getAutresDettes());
            addAutresBiensSection(document, dto.getAutresBiensDeValeur());
            addAppareilsElectroSection(document, dto.getAppareilsElectromenagers());
    
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
    
   
  private void addAssujettiSection(Document document, Assujetti assujetti) {
    if (assujetti == null) return;

    // Title section
    Text titleText = new Text("1. Informations Personnelles");
    titleText.setBold();
    Paragraph title = new Paragraph(titleText);
    title.setFontSize(14);
    title.setMarginBottom(10);

    // Create table with columns "Champ" and "Valeur"
    Table table = createDynamicTable("Champ", "Valeur");

    // Add personal information
    addRow(table, "Nom", assujetti.getNom());
    addRow(table, "Prénom", assujetti.getPrenom());
    addRow(table, "Civilité", assujetti.getCivilite() != null ? assujetti.getCivilite().getIntitule() : "N/A");
    addRow(table, "Téléphone", assujetti.getContacttel());
    addRow(table, "Email", assujetti.getEmail());
    addRow(table, "Code", assujetti.getCode());
    addRow(table, "État", assujetti.getEtat().toString()); // Adjust if EtatAssujettiEnum has a display name method

    // Institutional information
    addRow(table, "Institution", assujetti.getInstitutions().getIntitule());
    addRow(table, "Administration", assujetti.getAdministration().getIntitule());
    addRow(table, "Entité", assujetti.getEntite().getIntitule());
    addRow(table, "Fonction", assujetti.getFonction().getIntitule());
    addRow(table, "Matricule", assujetti.getMatricule());

    // Date formatting
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    addRow(table, "Date prise de service", dateFormat.format(assujetti.getDatePriseDeService()));
    //String dateCessation = assujetti.getDateCessationFonction() != null 
                            //? dateFormat.format(assujetti.getDateCessationFonction()) 
                            //: "N/A";
    //addRow(table, "Date cessation fonction", dateCessation);



    // Add title and table to the document
    document.add(title);
    document.add(table);
}
    
    private void addVehiculesSection(Document document, List<VehiculeProjection> vehicules) {
        if (isEmpty(vehicules)) return;
        
        Paragraph title = new Paragraph("2. Véhicules")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Désignation", "Marque", "Immatriculation", 
            "Année", "Valeur (FCFA)"
        );
        
        for (VehiculeProjection v : vehicules) {
            addRow(table,
                v.getDesignation() != null ? v.getDesignation().getIntitule() : "N/A",
                v.getMarque() != null ? v.getMarque().getIntitule() : "N/A",
                v.getImmatriculation(),
                String.valueOf(v.getAnneeAcquisition()),
                formatMoney(v.getValeurAcquisition())
            );
        }
        
        document.add(title);
        document.add(table);
        //        //        //document.add(new AreaBreak());
    }
        
    private void addRevenusSection(Document document, List<RevenusProjection> revenus) {
        if (isEmpty(revenus)) return;
        
        Paragraph title = new Paragraph("3. Revenus")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Salaire Mensuel Net", "Autres Revenus", "Date"
        );
        
        for (RevenusProjection r : revenus) {
            addRow(table,
                formatMoney(r.getSalaireMensuelNet()),
                r.getAutresRevenus() != null ? r.getAutresRevenus().getIntitule() : "N/A",
                r.getDateCreation() != null ? r.getDateCreation().toString() : "N/A"
            );
        }
        
        document.add(title);
        document.add(table);
        //        //        //document.add(new AreaBreak());
    }
    
    
    private String formatMoney(Float amount) {
        return amount != null ? String.format("%,.2f FCFA", amount) : "N/A";
    }
    
    private String formatDate(LocalDate date) {
        return date != null ? date.toString() : "N/A";
    }
    
    private String getVocabValue(Vocabulaire vocab) {
        return (vocab != null) ? vocab.getIntitule() : "N/A";
    }
    
    private boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    private void addMainHeader(Document document, DeclarationDto dto) {
        Text headerText = new Text("Déclaration de Biens");
        headerText.setBold();
        Paragraph header = new Paragraph(headerText);
        header.setFontSize(16);
        header.setTextAlignment(TextAlignment.CENTER);
        document.add(header);
    
        Paragraph details = new Paragraph();
        details.add(new Text("Date de Déclaration: " + dto.getDateDeclaration()));
        details.add(new Text("\nType de Déclaration: " + dto.getTypeDeclaration()));
        document.add(details);
        
        //        //        //document.add(new AreaBreak());
    }

    private Table createDynamicTable(String... headers) {
        float[] columnWidths = new float[headers.length];
        Arrays.fill(columnWidths, 1f); 
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();        
        
        for (String header : headers) {
            table.addHeaderCell(createHeaderCell(header));
        }
        
        return table.setMarginTop(10);
    }
    
    private void addRow(Table table, String... values) {
        for (String value : values) {
            table.addCell(createCell(value));
        }
    }
    
    private Cell createHeaderCell(String text) {
        Text headerText = new Text(text).setBold().setFontSize(12);
        Paragraph paragraph = new Paragraph(headerText);
        paragraph.setMultipliedLeading(0.9f);
        
        Cell cell = new Cell();
        cell.add(paragraph);
        cell.setTextAlignment(TextAlignment.LEFT);
        cell.setPadding(5);
        
        return cell;
    }
    
    private Cell createCell(String text) {
        Text cellText = new Text(text != null ? text : "N/A").setFontSize(10);
        Paragraph paragraph = new Paragraph(cellText);
        paragraph.setMultipliedLeading(0.9f);
        
        Cell cell = new Cell();
        cell.add(paragraph);
        cell.setTextAlignment(TextAlignment.LEFT);
        cell.setPadding(5);
        
        return cell;
    }
    
    private void addFoncierBatiSection(Document document, List<FoncierBatiProjection> fonciers) {
        if (isEmpty(fonciers)) return;
        
        Paragraph title = new Paragraph("4. Biens Fonciers Bâtis")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Nature", "Année Construction", "Mode Acquisition", "Références Cadastrales",
            "Superficie", "Localisation", "Usage", "Coût Acquisition (FCFA)", "Investissements (FCFA)"
        );
        
        for (FoncierBatiProjection f : fonciers) {
            addRow(table,
                getVocabValue(f.getNature()),
                String.valueOf(f.getAnneeConstruction()),
                getVocabValue(f.getModeAcquisition()),
                f.getReferencesCadastrales(),
                f.getSuperficie(),
                getVocabValue(f.getLocalis()),
                getVocabValue(f.getTypeUsage()),
                formatMoney(f.getCoutAcquisitionFCFA()),
                formatMoney(f.getCoutInvestissements())
            );
        }
        
        document.add(title);
        document.add(table);
        //document.add(new AreaBreak());
    }
    
    private void addFoncierNonBatiSection(Document document, List<FoncierNonBatiProjection> fonciers) {
        if (isEmpty(fonciers)) return;
        
        Paragraph title = new Paragraph("5. Biens Fonciers Non Bâtis")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Nature", "Mode Acquisition", "Ilot", "Lotissement", 
            "Superficie", "Localité", "Titre Propriété", "Année Acquisition",
            "Valeur (FCFA)", "Investissements (FCFA)"
        );
        
        for (FoncierNonBatiProjection f : fonciers) {
            addRow(table,
                getVocabValue(f.getNature()),
                getVocabValue(f.getModeAcquisition()),
                f.getIlot(),
                f.getLotissement(),
                f.getSuperficie(),
                f.getLocalite(),
                f.getTitrePropriete(),
                String.valueOf(f.getDateAcquis()),
                formatMoney(f.getValeurAcquisFCFA()),
                formatMoney(f.getCoutInvestissements())
            );
        }
        
        document.add(title);
        document.add(table);
                //        //document.add(new AreaBreak());
    }
    
    private void addEmpruntsSection(Document document, List<EmpruntsProjection> emprunts) {
        if (isEmpty(emprunts)) return;
        
        Paragraph title = new Paragraph("6. Emprunts")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Institution", "Numéro Compte", "Type", "Montant (FCFA)"
        );
        
        for (EmpruntsProjection e : emprunts) {
            addRow(table,
                getVocabValue(e.getInstitutionsFinancieres()),
                e.getNumeroCompte(),
                getVocabValue(e.getTypeEmprunt()),
                formatMoney(e.getMontantEmprunt())
            );
        }
        
        document.add(title);
        document.add(table);
        //        //        //document.add(new AreaBreak());
    }
    
    private void addDisponibilitesBanqueSection(Document document, List<DisponibilitesEnBanqueProjection> disponibilites) {
        if (isEmpty(disponibilites)) return;
        
        Paragraph title = new Paragraph("7. Disponibilités Bancaires")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Banque", "Numéro Compte", "Type Compte", "Solde (FCFA)", "Date Solde"
        );
        
        for (DisponibilitesEnBanqueProjection d : disponibilites) {
            addRow(table,
                getVocabValue(d.getBanque()),
                d.getNumeroCompte(),
                getVocabValue(d.getTypeCompte()),
                formatMoney(d.getSoldeFCFA()),
                formatDate(d.getDateSolde())
            );
        }
        
        document.add(title);
        document.add(table);
        //        //        //document.add(new AreaBreak());
    }
    
    private void addEspecesSection(Document document, List<EspecesProjection> especes) {
        if (isEmpty(especes)) return;
        
        Paragraph title = new Paragraph("8. Espèces")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Monnaie", "Devise", "Taux Change", "Montant FCFA", "Total FCFA", "Date"
        );
        
        for (EspecesProjection e : especes) {
            addRow(table,
                formatMoney(e.getMonnaie()),
                formatMoney(e.getDevise()),
                String.valueOf(e.getTauxChange()),
                formatMoney(e.getMontantFCFA()),
                formatMoney(e.getMontantTotalFCFA()),
                formatDate(e.getDateEspece())
            );
        }
        
        document.add(title);
        document.add(table);
        //        //        //document.add(new AreaBreak());
    }
    
    private void addAnimauxSection(Document document, List<AnimauxProjection> animaux) {
        if (isEmpty(animaux)) return;
        
        Paragraph title = new Paragraph("9. Animaux")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Espèce", "Nombre Têtes", "Mode Acquisition", "Année", "Valeur (FCFA)", "Localité"
        );
        
        for (AnimauxProjection a : animaux) {
            addRow(table,
                a.getEspeces(),
                String.valueOf(a.getNombreTetes()),
                getVocabValue(a.getModeAcquisition()),
                String.valueOf(a.getAnneeAcquisition()),
                formatMoney(a.getValeurAcquisition()),
                getVocabValue(a.getLocalite())
            );
        }
        
        document.add(title);
        document.add(table);
        //        //        //document.add(new AreaBreak());
    }
    
    private void addMeublesSection(Document document, List<MeublesMeublantsProjection> meubles) {
        if (isEmpty(meubles)) return;
        
        Paragraph title = new Paragraph("10. Meubles Meublants")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Désignation", "Année", "Valeur (FCFA)", "État"
        );
        
        for (MeublesMeublantsProjection m : meubles) {
            addRow(table,
                getVocabValue(m.getDesignation()),
                String.valueOf(m.getAnneeAcquisition()),
                formatMoney(m.getValeurAcquisition()),
                getVocabValue(m.getEtatGeneral())
            );
        }
        
        document.add(title);
        document.add(table);
        //        //        //        //document.add(new AreaBreak());
    }
    
    private void addTitresSection(Document document, List<TitresProjection> titres) {
        if (isEmpty(titres)) return;
        
        Paragraph title = new Paragraph("11. Titres")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Nature/Actions", "Valeur (FCFA)", "Emplacement", "Précisions"
        );
        
        for (TitresProjection t : titres) {
            addRow(table,
                getVocabValue(t.getDesignationNatureActions()),
                formatMoney(t.getValeurEmplacement()),
                getVocabValue(t.getEmplacement()),
                getVocabValue(t.getAutrePrecisions())
            );
        }
        
        document.add(title);
        document.add(table);
                //        //        //document.add(new AreaBreak());
    }
    
    private void addCreancesSection(Document document, List<LesCreancesProjection> creances) {
        if (isEmpty(creances)) return;
        
        Paragraph title = new Paragraph("12. Créances")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Débiteur", "Montant (FCFA)", "Précisions"
        );
        
        for (LesCreancesProjection c : creances) {
            addRow(table,
                getVocabValue(c.getDebiteurs()),
                formatMoney(c.getMontant()),
                getVocabValue(c.getAutresPrecisions())
            );
        }
        
        document.add(title);
        document.add(table);
                //        //        //document.add(new AreaBreak());
    }
    
    private void addAutresDettesSection(Document document, List<AutresDettesProjection> dettes) {
        if (isEmpty(dettes)) return;
        
        Paragraph title = new Paragraph("13. Autres Dettes")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Créancier", "Montant (FCFA)", "Justificatifs", "Précisions"
        );
        
        for (AutresDettesProjection d : dettes) {
            addRow(table,
                getVocabValue(d.getCreanciers()),
                formatMoney(d.getMontant()),
                getVocabValue(d.getJustificatifs()),
                getVocabValue(d.getAutresPrecisions())
            );
        }
        
        document.add(title);
        document.add(table);
                //        //        //document.add(new AreaBreak());
    }
    
    private void addAutresBiensSection(Document document, List<AutresBiensDeValeurProjection> biens) {
        if (isEmpty(biens)) return;
        
        Paragraph title = new Paragraph("14. Autres Biens de Valeur")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Désignation", "Localité", "Année", "Valeur (FCFA)", "Précisions", "Type"
        );
        
        for (AutresBiensDeValeurProjection b : biens) {
            addRow(table,
                getVocabValue(b.getDesignation()),
                getVocabValue(b.getLocalite()),
                String.valueOf(b.getAnneeAcquis()),
                formatMoney(b.getValeurAcquisition()),
                getVocabValue(b.getAutrePrecisions()),
                getVocabValue(b.getType())
            );
        }
        
        document.add(title);
        document.add(table);
                //        //        //document.add(new AreaBreak());
    }
    
    private void addAppareilsElectroSection(Document document, List<AppareilsElectroMenagersProjection> appareils) {
        if (isEmpty(appareils)) return;
        
        Paragraph title = new Paragraph("15. Appareils Électroménagers")
            .setBold().setFontSize(14).setMarginBottom(10);
        
        Table table = createDynamicTable(
            "Désignation", "Année", "Valeur (FCFA)", "État"
        );
        
        for (AppareilsElectroMenagersProjection a : appareils) {
            addRow(table,
                getVocabValue(a.getDesignation()),
                String.valueOf(a.getAnneeAcquisition()),
                formatMoney(a.getValeurAcquisition()),
                getVocabValue(a.getEtatGeneral())
            );
        }
        
        document.add(title);
        document.add(table);
         //document.add(new AreaBreak());
    }

}