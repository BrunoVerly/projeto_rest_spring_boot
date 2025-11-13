package com.example.projetoRestSpringBoot.service;

import com.example.projetoRestSpringBoot.config.EmailConfig;
import com.example.projetoRestSpringBoot.dto.request.EmailRequestDTO;
import com.example.projetoRestSpringBoot.exception.EmailSendingException;
import com.example.projetoRestSpringBoot.mail.EmailSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class EmailService {
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private EmailConfig emailConfig;

    public void sentSimpleEmail(EmailRequestDTO emailRequest){
        try {
            emailSender
                    .to(emailRequest.getTo())
                    .withSubject(emailRequest.getSubject())
                    .withMessage(emailRequest.getBody())
                    .send(emailConfig);
        } catch (EmailSendingException e) {
            throw e;
        } catch (Exception e) {
            throw new EmailSendingException("Erro ao enviar e-mail simples: " + e.getMessage(), e);
        }
    }

    public void senEmailWithAttachment(String emailRequestJson, MultipartFile attachment){
        File tempFile = null;
        try {
            EmailRequestDTO emailRequest = new ObjectMapper().readValue(emailRequestJson, EmailRequestDTO.class);
            tempFile = File.createTempFile("attachment", attachment.getOriginalFilename());
            attachment.transferTo(tempFile);

            emailSender
                    .to(emailRequest.getTo())
                    .withSubject(emailRequest.getSubject())
                    .withMessage(emailRequest.getBody())
                    .attach(tempFile.getAbsolutePath())
                    .send(emailConfig);

        } catch (JsonProcessingException e) {
            throw new EmailSendingException("Erro ao converter o e-mail para JSON: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new EmailSendingException("Erro ao processar o anexo: " + e.getMessage(), e);
        } catch (EmailSendingException e) {
            throw e;
        } catch (Exception e) {
            throw new EmailSendingException("Erro ao enviar e-mail com anexo: " + e.getMessage(), e);
        }
        finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
