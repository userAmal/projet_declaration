package com.informatization_controle_declarations_biens.declaration_biens_control.service.control;

import java.io.FileNotFoundException;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.informatization_controle_declarations_biens.declaration_biens_control.dto.declaration.DeclarationDto;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Declaration;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.declaration.IDeclarationService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;


@Service
public class PdfFileService {
	
	@Autowired
	private IDeclarationService declarationService;
	
	public void pdfCreation() {
		String filepath = "E:\\pfe_projet\\declaration\\rapport\\nv-rapport.pdf";
		
		try {
			PdfWriter writer = new PdfWriter(filepath);
			PdfDocument pdfDoc = new PdfDocument(writer);
			pdfDoc.addNewPage();
			
			Document document = new Document(pdfDoc);
			
			Paragraph title = new Paragraph("Rapport des Déclarations de Biens")
					.setFontSize(18)
					.setBold()
					.setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER);
			document.add(title);
			
			List<Declaration> declarations = declarationService.findAll();
			
			addDeclarationsSummaryTable(document, declarations);
			
			for (Declaration declaration : declarations) {
				addDeclarationDetails(document, declaration.getId());
			}
			
			document.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void generateDeclarationPdf(Long declarationId, String filepath) {
		try {
			PdfWriter writer = new PdfWriter(filepath);
			PdfDocument pdfDoc = new PdfDocument(writer);
			pdfDoc.addNewPage();
			
			Document document = new Document(pdfDoc);
			
			addDeclarationDetails(document, declarationId);
			
			document.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void addDeclarationsSummaryTable(Document document, List<Declaration> declarations) {
		Paragraph subtitle = new Paragraph("Résumé des déclarations")
				.setFontSize(14)
				.setBold()
				.setMarginTop(20)
				.setMarginBottom(10);
		document.add(subtitle);
		
		Table table = new Table(com.itextpdf.layout.property.UnitValue.createPercentArray(new float[]{1, 3, 3, 3}));
		table.setWidth(com.itextpdf.layout.property.UnitValue.createPercentValue(100));
		
		table.addHeaderCell(createHeaderCell("ID"));
		table.addHeaderCell(createHeaderCell("Date de déclaration"));
		table.addHeaderCell(createHeaderCell("Déclarant"));
		table.addHeaderCell(createHeaderCell("État"));
		
		for (Declaration declaration : declarations) {
			table.addCell(createCell(String.valueOf(declaration.getId())));
			table.addCell(createCell(declaration.getDateDeclaration() != null ? 
					declaration.getDateDeclaration().toString() : "N/A"));
			table.addCell(createCell(declaration.getAssujetti() != null ? 
					declaration.getAssujetti().getNom() + " " + declaration.getAssujetti().getPrenom() : "N/A"));
			table.addCell(createCell(declaration.getEtatDeclaration() != null ? 
					declaration.getEtatDeclaration().toString() : "N/A"));
		}
		
		document.add(table);
	}
	
	private void addDeclarationDetails(Document document, Long declarationId) {
		DeclarationDto declarationDto = declarationService.getFullDeclarationDetails(declarationId);
		
		if (declarationDto == null) {
			return;
		}
		
		document.add(new Paragraph("\n").setMarginTop(30));
		
		Paragraph declTitle = new Paragraph("Détails de la Déclaration #" + declarationId)
				.setFontSize(16)
				.setBold()
				.setTextAlignment(TextAlignment.CENTER);
		document.add(declTitle);
		
		addBasicInfo(document, declarationDto);
		
		if (declarationDto.getFonciersBatis() != null && !declarationDto.getFonciersBatis().isEmpty()) {
			addSectionTitle(document, "Biens Immobiliers Bâtis");
			addFoncierBatiTable(document, declarationDto);
		}
		
		if (declarationDto.getFonciersNonBatis() != null && !declarationDto.getFonciersNonBatis().isEmpty()) {
			addSectionTitle(document, "Biens Immobiliers Non Bâtis");
			addFoncierNonBatiTable(document, declarationDto);
		}
		
		if (declarationDto.getVehicules() != null && !declarationDto.getVehicules().isEmpty()) {
			addSectionTitle(document, "Véhicules");
			addVehiculeTable(document, declarationDto);
		}
		
		if (declarationDto.getDisponibilitesBanque() != null && !declarationDto.getDisponibilitesBanque().isEmpty()) {
			addSectionTitle(document, "Disponibilités en Banque");
			addDisponibilitesBanqueTable(document, declarationDto);
		}
		

		if (declarationDto.getEspeces() != null && !declarationDto.getEspeces().isEmpty()) {
			addSectionTitle(document, "Espèces");
			addEspecesTable(document, declarationDto);
		}
		

		addFinancialSummary(document, declarationDto);
	}
	
	private void addBasicInfo(Document document, DeclarationDto declarationDto) {
		Table infoTable = new Table(UnitValue.createPercentArray(new float[]{2, 3}));
		infoTable.setWidth(UnitValue.createPercentValue(100));
		
		String nomPrenom = "N/A";
		if (declarationDto.getAssujetti() != null) {
			nomPrenom = declarationDto.getAssujetti().getNom() + " " + declarationDto.getAssujetti().getPrenom();
		}
		
		infoTable.addCell(createCell("Nom et prénom du déclarant:"));
		infoTable.addCell(createCell(nomPrenom));
		
		infoTable.addCell(createCell("Date de déclaration:"));
		infoTable.addCell(createCell(declarationDto.getDateDeclaration() != null ? 
				declarationDto.getDateDeclaration().toString() : "N/A"));
		

		infoTable.addCell(createCell("Fonction:"));

		
		infoTable.addCell(createCell("Type de déclaration:"));
		infoTable.addCell(createCell(declarationDto.getTypeDeclaration() != null ? 
				declarationDto.getTypeDeclaration().toString() : "N/A"));
		
		infoTable.addCell(createCell("État de la déclaration:"));
		infoTable.addCell(createCell(declarationDto.getEtatDeclaration() != null ? 
				declarationDto.getEtatDeclaration().toString() : "N/A"));
		
		document.add(infoTable);
	}
	
	private void addSectionTitle(Document document, String title) {
		Paragraph sectionTitle = new Paragraph(title)
				.setFontSize(14)
				.setBold()
				.setMarginTop(15)
				.setMarginBottom(5);
		document.add(sectionTitle);
	}
	
	private void addFoncierBatiTable(Document document, DeclarationDto declarationDto) {
		Table table = new Table(com.itextpdf.layout.property.UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 2}));
		table.setWidth(com.itextpdf.layout.property.UnitValue.createPercentValue(100));
		

		table.addHeaderCell(createHeaderCell("ID"));
		table.addHeaderCell(createHeaderCell("Description"));
		table.addHeaderCell(createHeaderCell("Adresse"));
		table.addHeaderCell(createHeaderCell("Superficie"));
		table.addHeaderCell(createHeaderCell("Valeur estimée"));
		

		declarationDto.getFonciersBatis().forEach(foncier -> {
			table.addCell(createCell(String.valueOf(foncier.getId())));

			table.addCell(createCell(foncier.getSuperficie() != null ? foncier.getSuperficie().toString() + " m²" : "N/A"));

		});
		
		document.add(table);
	}
	
	private void addFoncierNonBatiTable(Document document, DeclarationDto declarationDto) {
		Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 2}));
		table.setWidth(UnitValue.createPercentValue(100));
		

		table.addHeaderCell(createHeaderCell("ID"));
		table.addHeaderCell(createHeaderCell("Type de terrain"));
		table.addHeaderCell(createHeaderCell("Localisation"));
		table.addHeaderCell(createHeaderCell("Superficie"));
		table.addHeaderCell(createHeaderCell("Valeur estimée"));
		

		declarationDto.getFonciersNonBatis().forEach(foncier -> {
			table.addCell(createCell(String.valueOf(foncier.getId())));
;
			table.addCell(createCell(foncier.getSuperficie() != null ? foncier.getSuperficie().toString() + " m²" : "N/A"));

		});
		
		document.add(table);
	}
	
	private void addVehiculeTable(Document document, DeclarationDto declarationDto) {
		Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 1, 2, 2}));
		table.setWidth(UnitValue.createPercentValue(100));
		
		table.addHeaderCell(createHeaderCell("ID"));
		table.addHeaderCell(createHeaderCell("Marque"));
		table.addHeaderCell(createHeaderCell("Modèle"));
		table.addHeaderCell(createHeaderCell("Année"));
		table.addHeaderCell(createHeaderCell("Immatriculation"));
		table.addHeaderCell(createHeaderCell("Valeur estimée"));
		

		declarationDto.getVehicules().forEach(vehicule -> {
			table.addCell(createCell(String.valueOf(vehicule.getId())));

			table.addCell(createCell(vehicule.getImmatriculation() != null ? vehicule.getImmatriculation() : "N/A"));

		});
		
		document.add(table);
	}
	
	private void addDisponibilitesBanqueTable(Document document, DeclarationDto declarationDto) {
		Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 3}));
		table.setWidth(UnitValue.createPercentValue(100));
		

		table.addHeaderCell(createHeaderCell("ID"));
		table.addHeaderCell(createHeaderCell("Banque"));
		table.addHeaderCell(createHeaderCell("Type de compte"));
		table.addHeaderCell(createHeaderCell("Montant"));
		
		declarationDto.getDisponibilitesBanque().forEach(disponibilite -> {
			table.addCell(createCell(String.valueOf(disponibilite.getId())));
		});
		
		document.add(table);
	}
	
	private void addEspecesTable(Document document, DeclarationDto declarationDto) {
		Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 3}));
		table.setWidth(UnitValue.createPercentValue(100));
		
		table.addHeaderCell(createHeaderCell("ID"));
		table.addHeaderCell(createHeaderCell("Description"));
		table.addHeaderCell(createHeaderCell("Montant"));
		
		declarationDto.getEspeces().forEach(espece -> {
			table.addCell(createCell(String.valueOf(espece.getId())));
		});
		
		document.add(table);
	}
	
	private void addFinancialSummary(Document document, DeclarationDto declarationDto) {
		addSectionTitle(document, "Synthèse Financière");
		
		double totalActifs = calculerTotalActifs(declarationDto);
		double totalPassifs = calculerTotalPassifs(declarationDto);
		double patrimoineNet = totalActifs - totalPassifs;
		
		Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{3, 2}));
		summaryTable.setWidth(UnitValue.createPercentValue(100));
		

		Cell cellLabelActifs = new Cell();
		cellLabelActifs.add(new Paragraph("Total des actifs").setBold());
		summaryTable.addCell(cellLabelActifs);
		
		Cell cellValueActifs = new Cell();
		cellValueActifs.add(new Paragraph(String.format("%,.2f DA", totalActifs)));
		summaryTable.addCell(cellValueActifs);
		
		Cell cellLabelPassifs = new Cell();
		cellLabelPassifs.add(new Paragraph("Total des passifs").setBold());
		summaryTable.addCell(cellLabelPassifs);
		
		Cell cellValuePassifs = new Cell();
		cellValuePassifs.add(new Paragraph(String.format("%,.2f DA", totalPassifs)));
		summaryTable.addCell(cellValuePassifs);
		
		Cell cellLabelNet = new Cell();
		cellLabelNet.add(new Paragraph("Patrimoine net").setBold());
		summaryTable.addCell(cellLabelNet);
		
		Cell cellValueNet = new Cell();
		cellValueNet.add(new Paragraph(String.format("%,.2f DA", patrimoineNet)));
		summaryTable.addCell(cellValueNet);
		
		document.add(summaryTable);
	}
	
	private double calculerTotalActifs(DeclarationDto declarationDto) {
		double total = 0.0;
		
	
		if (declarationDto.getFonciersBatis() != null) {
		}
		
		if (declarationDto.getFonciersNonBatis() != null) {

		}
		
		if (declarationDto.getVehicules() != null) {
		}
		
		if (declarationDto.getDisponibilitesBanque() != null) {

		}
		
		if (declarationDto.getEspeces() != null) {

		}
		
		
		return total;
	}
	
	private double calculerTotalPassifs(DeclarationDto declarationDto) {
		double total = 0.0;
		
		if (declarationDto.getEmprunts() != null) {

		}
		
		if (declarationDto.getAutresDettes() != null) {
		}
		
		return total;
	}
	
	private Cell createHeaderCell(String text) {
		Cell cell = new Cell();
		cell.add(new Paragraph(text).setBold());
		cell.setTextAlignment(TextAlignment.CENTER);
		return cell;
	}
	
	private Cell createCell(String text) {
		Cell cell = new Cell();
		cell.add(new Paragraph(text));
		return cell;
	}
}