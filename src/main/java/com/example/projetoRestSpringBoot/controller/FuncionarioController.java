package com.example.projetoRestSpringBoot.controller;

import com.example.projetoRestSpringBoot.controller.docs.FuncionariosControllerDocs;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.dto.IntervaloDataDTO;
import com.example.projetoRestSpringBoot.enums.FuncionarioSituacao;
import com.example.projetoRestSpringBoot.exception.BadRequestException;
import com.example.projetoRestSpringBoot.file.exporter.MediaTypes;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.service.FuncionarioService;
import com.example.projetoRestSpringBoot.service.linkhateoas.HateoasLinkManager;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/funcionario/v1")
public class FuncionarioController implements FuncionariosControllerDocs {
    @Autowired
    private FuncionarioService service;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<PagedModel<EntityModel<FuncionarioDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
        var result = service.findAll(pageable);
        HateoasLinkManager.addFuncionarioListPageLinks(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/buscarPorNome/{nome}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<FuncionarioDTO>>> findByName(
            @PathVariable("nome") String nome,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
        var result = service.findByName(nome, pageable);
        HateoasLinkManager.addFuncionarioListPageLinks(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/buscarPorSituacao/{situacao}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<PagedModel<EntityModel<FuncionarioDTO>>> findBySituacao(
            @PathVariable("situacao") FuncionarioSituacao situacao,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
        var result = service.findBySituacao(situacao, pageable);
        HateoasLinkManager.addFuncionarioListPageLinks(result);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/buscarPorAdmissao",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<FuncionarioDTO>>> findByAdmissao(
            @RequestBody IntervaloDataDTO intervalo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        if (intervalo == null || intervalo.getStartDate() == null || intervalo.getEndDate() == null) {
            throw new BadRequestException("Datas de início e fim são obrigatórias");
        }

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
        var result = service.findFuncionarioByAddmitedDate(intervalo.getStartDate(), intervalo.getEndDate(), pageable);
        HateoasLinkManager.addFuncionarioListPageLinks(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/buscarPorId/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    @Override
    public FuncionarioDTO findById (@PathVariable("id") long id) {
        var funcionario = service.findById(id);
        return funcionario;
    }

    @GetMapping(value = "buscarPorMatricula/{matricula}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
    @Override
    public FuncionarioDTO findByMatricula(@PathVariable("matricula") String matricula) {
        var funcionario = service.findByMatricula(matricula);
        return funcionario;
    }

    @PostMapping(consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Override
    public Funcionario create (@RequestBody Funcionario funcionario) {
        return service.create(funcionario);
    }

    @PostMapping(value="/importar",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    //@Override
    public List<FuncionarioDTO> importarFuncionarios (@RequestParam ("file") MultipartFile file) {
        return service.importarArquivo(file);
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
    public FuncionarioDTO update(@RequestBody FuncionarioDTO funcionario) {
        return service.update(funcionario);
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
    public ResponseEntity<Resource> exportPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            HttpServletRequest request
    ){
        if (page < 0 || size <= 0) {
            throw new BadRequestException("Parâmetros de paginação inválidos: page >= 0 e size > 0");
        }

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));

        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        Resource file = service.exportPage(pageable, acceptHeader);

        var contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";
        var fileExtension = MediaTypes.APPLICATION_TEXT_CSV_VALUE.equalsIgnoreCase(acceptHeader) ? ".csv" :
                MediaTypes.APPLICATION_PDF_VALUE.equalsIgnoreCase(acceptHeader) ? ".pdf" : "";

        var filename = "funcionarios" + fileExtension;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }
}
