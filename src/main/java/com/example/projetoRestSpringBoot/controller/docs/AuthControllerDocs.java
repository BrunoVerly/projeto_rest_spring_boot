package com.example.projetoRestSpringBoot.controller.docs;

import com.example.projetoRestSpringBoot.dto.security.AccountCredentialsDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AuthControllerDocs {
    @Operation(summary = "Autentica o usuário e retorna um token JWT")
    @PostMapping("/signin")
    ResponseEntity<?> signIn(@RequestBody AccountCredentialsDTO credentials);

    @Operation(summary = "Atualizar o token JWT usando um token de atualização")
    @PutMapping("/refresh/{username}")
    ResponseEntity<?> refreshToken(
            @PathVariable("username") String username,
            @RequestHeader("Authorization") String refreshToken);

    @PostMapping(value = "/criarUsuario",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    AccountCredentialsDTO create(@RequestBody AccountCredentialsDTO credentials);
}
