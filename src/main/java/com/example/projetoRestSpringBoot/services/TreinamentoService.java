package com.example.projetoRestSpringBoot.services;

import com.example.projetoRestSpringBoot.controller.TreinamentoController;
import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import com.example.projetoRestSpringBoot.model.Curso;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.model.Treinamento;
import com.example.projetoRestSpringBoot.repository.CursoRepository;
import com.example.projetoRestSpringBoot.repository.FuncionarioRepository;
import com.example.projetoRestSpringBoot.repository.TreinamentoRepository;
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
public class TreinamentoService {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = LoggerFactory.getLogger(TreinamentoService.class.getName());
    @Autowired
    TreinamentoRepository repository;

    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    CursoRepository cursoRepository;

    @Autowired(required = false)
    PagedResourcesAssembler<TreinamentoDTO> assembler;

    public PagedModel<EntityModel<TreinamentoDTO>> findAll(Pageable pageable) {
        logger.info(String.format("Procurando todos os treinamentos"));

        var people = repository.findAll(pageable);
        Page<TreinamentoDTO> peopleWithLinks = people.map(dto -> {
            TreinamentoDTO treinamento = parseObject(dto, TreinamentoDTO.class);
            addHateosLinks(treinamento);
            return treinamento;
        });
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(TreinamentoController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();
        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    public TreinamentoDTO findById(long id) {
        logger.info(String.format("Procurando um treinamento pelo Id"));

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado"));

        var dto = parseObject(entity, TreinamentoDTO.class);
        addHateosLinks(dto);
        return dto;
    }

    public Treinamento create(Treinamento treinamento) {
        logger.info("Criando um novo registro de treinamento no banco");

        if (treinamento == null) throw new RequiredObjectIsNullException();

        // Busca o funcionário e o curso
        Funcionario funcionario = funcionarioRepository.findById(treinamento.getFuncionario().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario não encontrado"));

        Curso curso = cursoRepository.findById(treinamento.getCurso().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        // Cria a entidade
        Treinamento entity = new Treinamento();
        entity.setFuncionario(funcionario);
        entity.setCurso(curso);
        entity.setDataAgendamento(treinamento.getDataAgendamento());
        entity.setDataConcluido(treinamento.getDataConcluido());
        entity.setDataVencimento(treinamento.getDataVencimento());
        entity.setInstrutor(treinamento.getInstrutor());
        entity.setStatus(treinamento.getStatus());

        // Salva e converte para DTO
        Treinamento savedEntity = repository.save(entity);
        var dto = parseObject(savedEntity, TreinamentoDTO.class);
        addHateosLinks(dto);

        return repository.save(entity);
    }

    public TreinamentoDTO update(TreinamentoDTO treinamentoDTO) {
        logger.info("Atualizando um treinamento no banco");

        if (treinamentoDTO == null) throw new RequiredObjectIsNullException();

        Treinamento entity = repository.findById(treinamentoDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Id não encontrado no banco"));

        entity.setDataAgendamento(treinamentoDTO.getDataAgendamento());
        entity.setDataVencimento(treinamentoDTO.getDataVencimento());
        entity.setDataConcluido(treinamentoDTO.getDataConcluido());
        entity.setInstrutor(treinamentoDTO.getInstrutor());
        entity.setStatus(treinamentoDTO.getStatus());

        // Salva e converte para DTO
        var dto = parseObject(repository.save(entity), TreinamentoDTO.class);
        addHateosLinks(dto);

        return dto;
    }


    public void delete(long id) {
        logger.info(String.format("Apagando um treinamento do banco"));

        Treinamento entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado no banco"));
        repository.delete(entity);
    }

    private static void addHateosLinks(TreinamentoDTO dto) {
        dto.add(linkTo(methodOn(TreinamentoController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(TreinamentoController.class).delete(dto.getId())).withRel("delete").withType("GET"));
        dto.add(linkTo(methodOn(TreinamentoController.class).create(parseObject(dto, Treinamento.class))).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(TreinamentoController.class).update(dto)).withRel("update").withType("PUT"));
        //dto.add(linkTo(methodOn(TreinamentoController.class).findByName("",1, 12, "asc")).withRel("findByName").withType("GET"));
    }

}

