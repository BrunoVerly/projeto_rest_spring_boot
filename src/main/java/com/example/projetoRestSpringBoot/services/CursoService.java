package com.example.projetoRestSpringBoot.services;

import com.example.projetoRestSpringBoot.controller.CursoController;
import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import com.example.projetoRestSpringBoot.model.Curso;
import com.example.projetoRestSpringBoot.repository.CursoRepository;
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
import java.util.concurrent.atomic.AtomicLong;
import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
public class CursoService {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = LoggerFactory.getLogger(CursoService.class.getName());
    @Autowired
    CursoRepository repository;
    
    @Autowired(required = false)
    PagedResourcesAssembler<CursoDTO> assembler;

    public PagedModel<EntityModel<CursoDTO>> findAll(Pageable pageable) {
        logger.info(String.format("Procurando todos os cursos"));

        var people = repository.findAll(pageable);
        Page<CursoDTO> peopleWithLinks = people.map(dto -> {
            CursoDTO curso = parseObject(dto, CursoDTO.class);
            addHateosLinks(curso);
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
        addHateosLinks(dto);
        return dto;
    }

    public Curso create(Curso curso) {
        logger.info(String.format("Criando um novo curso no banco"));

        if (curso == null) throw new RequiredObjectIsNullException();
        var entity = parseObject(curso, Curso.class);
        var dto = parseObject(repository.save(entity), CursoDTO.class);
        addHateosLinks(dto);
        return repository.save(entity);
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
        addHateosLinks(dto);
        return dto;

    }

    public void delete(long id) {
        logger.info(String.format("Apagando um curso do banco"));

        Curso entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado no banco"));
        repository.delete(entity);
    }

    private static void addHateosLinks(CursoDTO dto) {
        dto.add(linkTo(methodOn(CursoController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(CursoController.class).delete(dto.getId())).withRel("delete").withType("GET"));
        dto.add(linkTo(methodOn(CursoController.class).create(parseObject(dto, Curso.class))).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(CursoController.class).update(dto)).withRel("update").withType("PUT"));
        //dto.add(linkTo(methodOn(CursoController.class).findByName("",1, 12, "asc")).withRel("findByName").withType("GET"));
    }

}

