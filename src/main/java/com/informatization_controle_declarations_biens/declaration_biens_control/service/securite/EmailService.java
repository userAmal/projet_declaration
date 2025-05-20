package com.informatization_controle_declarations_biens.declaration_biens_control.service.securite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.parametrage.Parametrage;
import com.informatization_controle_declarations_biens.declaration_biens_control.service.parametrage.ParametrageService;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

@Service
public class EmailService {
    @Autowired
    private final JavaMailSender mailSender;
;
    private final String templatesPath;

    @Autowired
    public EmailService(JavaMailSender mailSender, ParametrageService parametrageService) {
        this.mailSender = mailSender;
        
        // Get template path from parameters
        Parametrage pathParam = parametrageService.getByCode("PATH_MODELES_MAIL");
        if (pathParam == null || pathParam.getValeur() == null) {
            throw new IllegalStateException("PATH_MODELES_MAIL parameter not configured");
        }
        this.templatesPath = pathParam.getValeur();
    }

    public void sendEmail(String to, String subject, String templateFileName, Map<String, Object> variables) {
        try {
            // Read template file
            Path templatePath = Paths.get(templatesPath, templateFileName + ".html");
            String htmlContent = Files.readString(templatePath, StandardCharsets.UTF_8);
            
            // Replace placeholders
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                htmlContent = htmlContent.replace("{" + entry.getKey() + "}", 
                        entry.getValue() != null ? entry.getValue().toString() : "");
            }
            
            // Send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}