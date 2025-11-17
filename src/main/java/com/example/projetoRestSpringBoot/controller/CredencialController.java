package com.example.projetoRestSpringBoot.controller;

import com.example.projetoRestSpringBoot.controller.docs.CredencialControllerDocs;
import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.dto.IntervaloDataDTO;
import com.example.projetoRestSpringBoot.enums.CredencialStatus;
import com.example.projetoRestSpringBoot.file.exporter.MediaTypes;
import com.example.projetoRestSpringBoot.model.Credencial;
import com.example.projetoRestSpringBoot.service.CredencialService;
import com.example.projetoRestSpringBoot.service.linkhateoas.HateoasLinkManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credencial/v1")
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
        var result = service.findAll(pageable);
        return ResponseEntity.ok(result);
    }


    @GetMapping(value = "/buscarPorId/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    @Override
    public CredencialDTO findById (@PathVariable("id") long id) {
        return service.findById(id);
    }

    @PostMapping(consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Override
    public Credencial create (@RequestBody CredencialDTO credencial) {
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataVencimento"));
        var result = service.findByStatus(status, pageable);
        return ResponseEntity.ok(result);
    }


    @PostMapping(value = "/buscarPorVencimento",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<PagedModel<EntityModel<CredencialDTO>>> findCredencialExpiring(
            @RequestBody IntervaloDataDTO intervalo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataVencimento"));
        var result = service.findCredencialExpiring(intervalo.getStartDate(), intervalo.getEndDate(), pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/buscarPorEmissao",
            consumes  = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<PagedModel<EntityModel<CredencialDTO>>> findCredencialEmited(
            @RequestBody IntervaloDataDTO intervalo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataEmissao"));
        var result = service.findCredencialEmited(intervalo.getStartDate(), intervalo.getEndDate(), pageable);
        return ResponseEntity.ok(result);
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

    @GetMapping(value = "/exportar", produces = {
            MediaTypes.APPLICATION_XLSX_VALUE,
            MediaTypes.APPLICATION_TEXT_CSV_VALUE,
            MediaTypes.APPLICATION_PDF_VALUE})
    public ResponseEntity<Resource> exportPage(HttpServletRequest request) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        Resource file = service.exportPage(acceptHeader);

        var contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";
        var fileExtension = MediaTypes.APPLICATION_TEXT_CSV_VALUE.equalsIgnoreCase(acceptHeader) ? ".csv" :
                MediaTypes.APPLICATION_PDF_VALUE.equalsIgnoreCase(acceptHeader) ? ".pdf" : "";

        var filename = "credenciais" + fileExtension;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }
}
