package com.example.projetoRestSpringBoot.controller;


import com.example.projetoRestSpringBoot.controller.docs.AuthControllerDocs;
import com.example.projetoRestSpringBoot.dto.security.AccountCredentialsDTO;
import com.example.projetoRestSpringBoot.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {

    @Autowired
    AuthService service;

    @PostMapping("/autenticar")
    @Override
    public ResponseEntity<?> signIn(@RequestBody AccountCredentialsDTO credentials) {
        if(credentials == null || StringUtils.isBlank(credentials.getUsername()) || StringUtils.isBlank(credentials.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Requisição invalida");
        }

        var token = service.signIn(credentials);

        if(token == null) {return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Requisição invalida");}

        return ResponseEntity.ok().body(token);
    }

    @PutMapping("/atualizar/{username}")
    @Override
    public ResponseEntity<?> refreshToken(
            @PathVariable("username") String username,
            @RequestHeader("Authorization") String refreshToken) {

        if(parametersAreInvalid(username, refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Requisição invalida");
        }
        var token = service.refreshToken(username, refreshToken);
        if(token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Requisição invalida");
        }
        return ResponseEntity.ok().body(token);
    }

    private boolean parametersAreInvalid(String username, String refreshToken) {
        return StringUtils.isBlank(username) || StringUtils.isBlank(refreshToken);
    }

    @PostMapping(value = "/criarUsuario",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Override
    public AccountCredentialsDTO create(@RequestBody AccountCredentialsDTO credentials) {
        return service.create(credentials);
    }


}
