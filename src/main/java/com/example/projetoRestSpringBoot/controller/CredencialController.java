package com.example.projetoRestSpringBoot.controller;

import com.example.projetoRestSpringBoot.controller.docs.CredencialControllerDocs;
import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.enums.CredencialStatus;
import com.example.projetoRestSpringBoot.model.Credencial;
import com.example.projetoRestSpringBoot.services.CredencialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/credencial/v1")
public class CredencialController implements CredencialControllerDocs {
    @Autowired
    private CredencialService service;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<PagedModel<EntityModel<CredencialDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
            ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "id"));
        return ResponseEntity.ok(service.findAll(pageable));
    }


    @GetMapping(value = "buscarPorId/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    @Override
    public CredencialDTO findById (@PathVariable("id") long id) {
        var credencial = service.findById(id);
        return credencial;
    }

    @PostMapping(consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
                produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Override
    public Credencial create (@RequestBody Credencial credencial) {
        return service.create(credencial);
    }
    @GetMapping(value = "/buscarPorStatus/{status}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<PagedModel<EntityModel<CredencialDTO>>> findByStatus(
            @PathVariable("status") CredencialStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
        return ResponseEntity.ok(service.findByStatus(status, pageable));
    }
    @GetMapping(value = "/buscarPorVencimento",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<CredencialDTO>>> findCredencialExpiring(
            @RequestBody Map<String, String> body,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        LocalDate startDate = body.containsKey("startDate") ? LocalDate.parse(body.get("startDate")) : LocalDate.now();
        LocalDate endDate = body.containsKey("endDate") ? LocalDate.parse(body.get("endDate")) : LocalDate.now();

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataVencimento"));
        return ResponseEntity.ok(service.findCredencialExpiring(startDate, endDate, pageable));

    }

    @GetMapping(value = "/buscarPorEmissao",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<CredencialDTO>>> findCredencialEmited(
            @RequestBody Map<String, String> body,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        LocalDate startDate = body.containsKey("startDate") ? LocalDate.parse(body.get("startDate")) : LocalDate.now();
        LocalDate endDate = body.containsKey("endDate") ? LocalDate.parse(body.get("endDate")) : LocalDate.now();

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataEmissao"));
        return ResponseEntity.ok(service.findCredencialEmited(startDate, endDate, pageable));

    }

    @PutMapping(consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE},
                produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Override
    public CredencialDTO update(@RequestBody CredencialDTO credencial) {
        return service.update(credencial);
    }


    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete (@PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
