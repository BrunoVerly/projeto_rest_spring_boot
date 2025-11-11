package com.example.projetoRestSpringBoot.controller;

import com.example.projetoRestSpringBoot.controller.docs.TreinamentoControllerDocs;
import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import com.example.projetoRestSpringBoot.enums.TreinamentoStatus;
import com.example.projetoRestSpringBoot.file.exporter.MediaTypes;
import com.example.projetoRestSpringBoot.model.Treinamento;
import com.example.projetoRestSpringBoot.services.TreinamentoService;
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

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/treinamento/v1")
public class TreinamentoController implements TreinamentoControllerDocs {
    @Autowired
    private TreinamentoService service;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<PagedModel<EntityModel<TreinamentoDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
            ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataVencimento"));
        return ResponseEntity.ok(service.findAll(pageable));
    }


    @GetMapping(value = "buscarPorId/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    @Override
    public TreinamentoDTO findById (@PathVariable("id") long id) {
        var treinamento = service.findById(id);
        return treinamento;
    }

    @GetMapping(value="/buscarPorStatus/{status}",produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<PagedModel<EntityModel<TreinamentoDTO>>> findByStatus(
            @PathVariable(value = "status") TreinamentoStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataVencimento"));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping(value="/buscarPorInstrutor",produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})
    //@Override
    public ResponseEntity<PagedModel<EntityModel<TreinamentoDTO>>> findByInstrutor(
            @RequestBody Map<String, String> body,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        String instrutor = body.containsKey("instrutor") ? body.get("instrutor") : "";
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataConcluido"));
        return ResponseEntity.ok(service.findByInstrutor(instrutor, pageable));
    }



    @GetMapping(value = "/buscarPorVencimento",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<TreinamentoDTO>>>  findTreinamentoExpiring(
            @RequestBody Map<String, String> body,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        LocalDate startDate = body.containsKey("startDate") ? LocalDate.parse(body.get("startDate")) : LocalDate.now();
        LocalDate endDate = body.containsKey("endDate") ? LocalDate.parse(body.get("endDate")) : LocalDate.now();

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataVencimento"));
        return ResponseEntity.ok(service.findTreinamentoExpiring(startDate, endDate, pageable));

    }

    @GetMapping(value = "/buscarPorConclusao",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<TreinamentoDTO>>>  findTreinamentoConluded(
            @RequestBody Map<String, String> body,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        LocalDate startDate = body.containsKey("startDate") ? LocalDate.parse(body.get("startDate")) : LocalDate.now();
        LocalDate endDate = body.containsKey("endDate") ? LocalDate.parse(body.get("endDate")) : LocalDate.now();

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataConcluido"));
        return ResponseEntity.ok(service.findTreinamentoConluded(startDate, endDate, pageable));

    }




    @PostMapping(consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
                produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Override
    public Treinamento create (@RequestBody Treinamento treinamento) {
        return service.create(treinamento);
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
    public TreinamentoDTO update(@RequestBody TreinamentoDTO treinamento) {
        return service.update(treinamento);
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
    @Override
    public ResponseEntity<Resource> exportPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            HttpServletRequest request
    ){
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataVencimento"));

        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        Resource file = service.exportPage(pageable, acceptHeader);

        var contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";
        var fileExtension = MediaTypes.APPLICATION_TEXT_CSV_VALUE.equalsIgnoreCase(acceptHeader) ? ".csv" :
                MediaTypes.APPLICATION_PDF_VALUE.equalsIgnoreCase(acceptHeader) ? ".pdf" : "";

        var filename = "treinamentos" + fileExtension;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }
}
