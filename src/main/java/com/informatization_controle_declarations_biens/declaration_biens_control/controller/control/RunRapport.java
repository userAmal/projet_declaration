package com.informatization_controle_declarations_biens.declaration_biens_control.controller.control;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.informatization_controle_declarations_biens.declaration_biens_control.service.control.PdfFileService;


@Component
public class RunRapport implements ApplicationRunner{
    

	@Autowired
    private PdfFileService pdfFileService;

	public RunRapport(PdfFileService pdfFileService) {
		
		this.pdfFileService = pdfFileService;
	}
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		System.out.println("Application Started to Run");
		pdfFileService.pdfCreation();
		System.out.println("Pdf File Got Created");
		
		
	}
}