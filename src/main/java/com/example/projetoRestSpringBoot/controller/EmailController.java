package com.example.projetoRestSpringBoot.controller;

import com.example.projetoRestSpringBoot.controller.docs.EmailControllerDocs;
import com.example.projetoRestSpringBoot.dto.request.EmailRequestDTO;
import com.example.projetoRestSpringBoot.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/email/v1")
public class EmailController implements EmailControllerDocs {

    @Autowired
    private EmailService service;

    @PostMapping
    @Override
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequest) {
        System.out.println("USER: " + System.getenv("EMAIL_USERNAME"));
        System.out.println("PASS: " + (System.getenv("EMAIL_PASSWORD") != null ? "****" : "NULL"));

        service.sentSimpleEmail(emailRequest);

        return new ResponseEntity<>("E-mail enviado com sucesso", HttpStatus.OK);
    }

    @PostMapping(value ="/anexo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam("emailRequest") String emailRequest,
            @RequestParam("attachment") MultipartFile attachment) {
        service.senEmailWithAttachment(emailRequest,  attachment);

        return new ResponseEntity<>("E-mail e anexo enviado com sucesso", HttpStatus.OK);
    }
}
