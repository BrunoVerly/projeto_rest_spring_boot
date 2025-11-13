package com.example.projetoRestSpringBoot.service;

import com.example.projetoRestSpringBoot.controller.CursoController;
import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.exception.BadRequestException;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import com.example.projetoRestSpringBoot.file.exporter.factory.FileExporterFactory;
import com.example.projetoRestSpringBoot.file.importer.contract.FileImporter;
import com.example.projetoRestSpringBoot.file.importer.factory.FileImporterFactory;
import com.example.projetoRestSpringBoot.model.Curso;
import com.example.projetoRestSpringBoot.repository.CursoRepository;
import com.example.projetoRestSpringBoot.service.linkhateoas.HateoasLinkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
public class CursoService {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = LoggerFactory.getLogger(CursoService.class.getName());
    @Autowired
    CursoRepository repository;
    @Autowired
    FileImporterFactory importer;
    @Autowired
    FileExporterFactory exporter;

    @Autowired(required = false)
    PagedResourcesAssembler<CursoDTO> assembler;

    public PagedModel<EntityModel<CursoDTO>> findAll(Pageable pageable) {
        logger.info(String.format("Procurando todos os cursos"));

        var people = repository.findAll(pageable);
        Page<CursoDTO> peopleWithLinks = people.map(dto -> {
            CursoDTO curso = parseObject(dto, CursoDTO.class);
            HateoasLinkManager.addCursoDetailLinks(curso);
            return curso;
        });
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(CursoController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();
        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    public CursoDTO findById(long id) {
        logger.info(String.format("Procurando um curso pelo Id"));

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado"));

        var dto = parseObject(entity, CursoDTO.class);
        HateoasLinkManager.addCursoDetailLinks(dto);
        return dto;
    }

    public Curso create(Curso curso) {
        logger.info(String.format("Criando um novo curso no banco"));

        if (curso == null) throw new RequiredObjectIsNullException();
        var entity = parseObject(curso, Curso.class);
        var dto = parseObject(repository.save(entity), CursoDTO.class);
        HateoasLinkManager.addCursoDetailLinks(dto);
        return repository.save(entity);
    }

    public PagedModel<EntityModel<CursoDTO>> findByName(String nome, Pageable pageable) {
        logger.info(String.format("Procurando curso(s) pelo nome"));

        var cursos = repository.findCursoByName(nome, pageable);
        var cursosLink = cursos.map(dto -> {
            var curso = parseObject(dto, CursoDTO.class);
            HateoasLinkManager.addCursoDetailLinks(curso);
            return curso;
        });
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(CursoController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();
        return assembler.toModel(cursosLink, findAllLink);
    }

    public CursoDTO update(CursoDTO curso) {
        logger.info(String.format("Atualizando um curso no banco"));

        if (curso == null) throw new RequiredObjectIsNullException();

        Curso entity = repository.findById(curso.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado no banco"));

        entity.setNome(curso.getNome());
        entity.setDescricao(curso.getDescricao());
        entity.setCargaHoraria(curso.getCargaHoraria());
        entity.setValidadeMeses(curso.getValidadeMeses());
        entity.setOrigemCurso(curso.getOrigemCurso());
        entity.setTipoObrigatoriedade(curso.getTipoObrigatoriedade());

        var dto = parseObject(repository.save(entity), CursoDTO.class);
        HateoasLinkManager.addCursoDetailLinks(dto);
        return dto;

    }

    public void delete(long id) {
        logger.info(String.format("Apagando um curso do banco"));

        Curso entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado no banco"));
        repository.delete(entity);
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Exportando a tabela de cursos no formato {}", acceptHeader);

        var cursos = repository.findAll(pageable)
                .map(curso -> parseObject(curso, CursoDTO.class))
                .getContent();

        try {
            FileExporter exporter = this.exporter.getExporter(acceptHeader);
            return exporter.exportCursos(cursos);
        } catch (Exception e) {
            logger.error("Erro ao exportar arquivo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao exportar o arquivo");
        }
    }

    public List<CursoDTO> importarArquivo(MultipartFile file) {
        logger.info(String.format("Importando cursos em massa a partir de um arquivo"));
        if (file.isEmpty()) throw new BadRequestException("Arquivo vazio");

        try (InputStream inputStream = file.getInputStream()) {
            String fileName = Optional.ofNullable(file.getOriginalFilename()).orElseThrow(() -> new BadRequestException("Nome do arquivo ausente"));
            FileImporter importer = this.importer.getImporter(fileName);

            List<Curso> entities = importer.importarCursos(inputStream).stream()
                    .map(dto -> repository.save(parseObject(dto, Curso.class)))
                    .toList();

            return entities.stream()
                    .map(entity -> {
                        var dto = parseObject(entity, CursoDTO.class);
                        HateoasLinkManager.addCursoDetailLinks(dto);
                        return dto;
                    })
                    .toList();
        } catch (Exception e) {
            throw new BadRequestException("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

}
