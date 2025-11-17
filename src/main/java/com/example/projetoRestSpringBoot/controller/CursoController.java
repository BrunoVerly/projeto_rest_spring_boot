package com.example.projetoRestSpringBoot.controller;

import com.example.projetoRestSpringBoot.controller.docs.CursoControllerDocs;
import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.file.exporter.MediaTypes;
import com.example.projetoRestSpringBoot.model.Curso;
import com.example.projetoRestSpringBoot.service.CursoService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/curso/v1")
public class CursoController implements CursoControllerDocs {
    @Autowired
    private CursoService service;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<PagedModel<EntityModel<CursoDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
        var result = service.findAll(pageable);
        HateoasLinkManager.addCursoListPageLinks(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/buscarPorNome/{nome}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<CursoDTO>>> findByName(
            @PathVariable("nome") String nome,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
        var result = service.findByName(nome, pageable);
        HateoasLinkManager.addCursoListPageLinks(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    @Override
    public CursoDTO findById (@PathVariable("id") long id) {
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
    public Curso create (@RequestBody Curso curso) {
        return service.create(curso);
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
    public CursoDTO update(@RequestBody CursoDTO curso) {
        return service.update(curso);
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

        var filename = "cursos" + fileExtension;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }


    @PostMapping(value="/importar",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    public List<CursoDTO> importarCursos (@RequestParam ("file") MultipartFile file) {
        return service.importarArquivo(file);
    }
}
