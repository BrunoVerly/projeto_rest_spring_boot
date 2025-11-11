package com.example.projetoRestSpringBoot.services;


import com.example.projetoRestSpringBoot.controller.FuncionarioController;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.enums.FuncionarioSituacao;
import com.example.projetoRestSpringBoot.exception.BadRequestException;
import com.example.projetoRestSpringBoot.exception.FileStorageException;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
//import com.example.projetoRestSpringBoot.file.importer.factory.FileImporterFactory;
import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import com.example.projetoRestSpringBoot.file.exporter.factory.FileExporterFactory;
import com.example.projetoRestSpringBoot.file.importer.contract.FileImporter;
import com.example.projetoRestSpringBoot.file.importer.factory.FileImporterFactory;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.repository.FuncionarioRepository;
import org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
public class FuncionarioService {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = LoggerFactory.getLogger(FuncionarioService.class.getName());
    @Autowired
    FuncionarioRepository repository;

    @Autowired
    FileImporterFactory importer;
    @Autowired
    FileExporterFactory exporter;

    @Autowired(required = false)
    PagedResourcesAssembler<FuncionarioDTO> assembler;

    public PagedModel<EntityModel<FuncionarioDTO>> findAll(Pageable pageable) {
        logger.info(String.format("Procurando todos os funcionarios"));

        var people = repository.findAll(pageable);
        Page<FuncionarioDTO> peopleWithLinks = people.map(dto -> {
            FuncionarioDTO person = parseObject(dto, FuncionarioDTO.class);
            addHateosLinks(person);
            return person;
        });
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(FuncionarioController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();
        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    public PagedModel<EntityModel<FuncionarioDTO>> findByName(String nome, Pageable pageable) {
        logger.info(String.format("Procurando um funcionario pelo nome"));

        var funcionarios = repository.findFuncionarioByName(nome, pageable);
        var funcionariosLinks = funcionarios.map(dto -> {
            var funcionario = parseObject(dto, FuncionarioDTO.class);
            addHateosLinks(funcionario);
            return funcionario;
        });
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(FuncionarioController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();
        return assembler.toModel(funcionariosLinks, findAllLink);
    }


    public FuncionarioDTO findById(long id) {
        logger.info(String.format("Procurando um funcionario pelo Id"));

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado"));

        var dto = parseObject(entity, FuncionarioDTO.class);
        addHateosLinks(dto);
        return dto;
    }
    public FuncionarioDTO findByMatricula(String matricula) {
        logger.info(String.format("Procurando um funcionario pela matricula"));

        var entity = repository.findByMatricula(matricula)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula nao encontrada"));

        var dto = parseObject(entity, FuncionarioDTO.class);
        addHateosLinks(dto);
        return dto;
    }
    public PagedModel<EntityModel<FuncionarioDTO>> findBySituacao(
            FuncionarioSituacao situacao, Pageable pageable) {

        logger.info("Procurando funcionários pela situação");

        // Busca a página de entidades
        Page<Funcionario> funcionariosPage = repository.findBySituacao(situacao, pageable);

        // Converte para DTO e adiciona links
        Page<FuncionarioDTO> funcionariosDTOPage = funcionariosPage.map(funcionario -> {
            FuncionarioDTO dto = parseObject(funcionario, FuncionarioDTO.class);
            addHateosLinks(dto);
            return dto;
        });

        // Cria link de página raiz
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(FuncionarioController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();

        // Converte para PagedModel
        return assembler.toModel(funcionariosDTOPage, findAllLink);
    }


    public Funcionario create(Funcionario funcionario) {
        logger.info(String.format("Criando um novo funcionario no banco"));

        if (funcionario == null) throw new RequiredObjectIsNullException();
        var entity = parseObject(funcionario, Funcionario.class);
        var dto = parseObject(repository.save(entity), FuncionarioDTO.class);
        addHateosLinks(dto);
        return repository.save(entity);
    }


    public FuncionarioDTO update(FuncionarioDTO funcionario) {
        logger.info(String.format("Atualizando um funcionario no banco"));

        if (funcionario == null) throw new RequiredObjectIsNullException();

        Funcionario entity = repository.findById(funcionario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado no banco"));

        entity.setNome(funcionario.getNome());
        entity.setMatricula(funcionario.getMatricula());
        entity.setCargo(funcionario.getCargo());
        entity.setDepartamento(funcionario.getDepartamento());
        entity.setDataAdmissao(funcionario.getDataAdmissao());
        entity.setSituacao(funcionario.getSituacao());
        entity.setTipoContrato(funcionario.getTipoContrato());
        entity.setEmail(funcionario.getEmail());
        entity.setTelefone(funcionario.getTelefone());

        var dto = parseObject(repository.save(entity), FuncionarioDTO.class);
        addHateosLinks(dto);
        return dto;

    }


    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Exportando a tabela de funcionarios no formato {}", acceptHeader);

        var funcionarios = repository.findAll(pageable)
                .map(funcionario -> parseObject(funcionario, FuncionarioDTO.class))
                .getContent();

        try {
            FileExporter exporter = this.exporter.getExporter(acceptHeader);
            return exporter.exportarFuncionarios(funcionarios);
        } catch (Exception e) {
            logger.error("Erro ao exportar arquivo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao exportar o arquivo");
        }
    }


    public PagedModel<EntityModel<FuncionarioDTO>> findFuncionarioByAddmitedDate(
            LocalDate startDate, LocalDate endDate, Pageable pageable) {

        logger.info("Procurando funcionários pela data de admissão");

        // Busca a página de entidades
        Page<Funcionario> funcionariosPage = repository.findFuncionarioByAddmitedDate(startDate, endDate, pageable);

        // Converte para DTO e adiciona links
        Page<FuncionarioDTO> funcionariosDTOPage = funcionariosPage.map(funcionario -> {
            FuncionarioDTO dto = parseObject(funcionario, FuncionarioDTO.class);
            addHateosLinks(dto);
            return dto;
        });

        // Cria link de página raiz
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(FuncionarioController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();

        // Converte para PagedModel
        return assembler.toModel(funcionariosDTOPage, findAllLink);
    }


    public void delete(long id) {
        logger.info(String.format("Apagando um funcionario do banco"));

        Funcionario entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado no banco"));
        repository.delete(entity);
    }

    private static void addHateosLinks(FuncionarioDTO dto) {
        dto.add(linkTo(methodOn(FuncionarioController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(FuncionarioController.class).delete(dto.getId())).withRel("delete").withType("GET"));
        dto.add(linkTo(methodOn(FuncionarioController.class).create(parseObject(dto, Funcionario.class))).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(FuncionarioController.class)).slash("importarArquivo").withRel("importarArquivo").withType("POST"));
        dto.add(linkTo(methodOn(FuncionarioController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(FuncionarioController.class).findByName("",1, 12, "asc")).withRel("findByName").withType("GET"));
        dto.add(linkTo(methodOn(FuncionarioController.class).exportPage(1, 12, "asc", null)).withRel("exportPage").withType("GET"));
    }


    public List<FuncionarioDTO> importarArquivo(MultipartFile file) {
        logger.info(String.format("Importando funcionários em massa a partir de um arquivo"));
        if (file.isEmpty()) throw new BadRequestException("Arquivo vazio");

        try (InputStream inputStream = file.getInputStream()) {
            String fileName = Optional.ofNullable(file.getOriginalFilename()).orElseThrow(() -> new BadRequestException("File name is missing"));
            FileImporter importer = this.importer.getImporter(fileName);


            List<Funcionario> entities = importer.importarFuncionarios(inputStream).stream()
                    .map(dto -> repository.save(parseObject(dto, Funcionario.class)))
                    .toList();

            return entities.stream()
                    .map(entity -> {
                        var  dto = parseObject(entity, FuncionarioDTO.class);
                        addHateosLinks(dto);
                        return dto;
                    })
                    .toList();
        } catch (Exception e) {
            throw new FileStorageException("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}

