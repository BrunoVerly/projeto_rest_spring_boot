package com.example.projetoRestSpringBoot.services;

import com.example.projetoRestSpringBoot.controller.FuncionarioController;
import com.example.projetoRestSpringBoot.controller.TreinamentoController;
import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import com.example.projetoRestSpringBoot.enums.FuncionarioSituacao;
import com.example.projetoRestSpringBoot.enums.TreinamentoStatus;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import com.example.projetoRestSpringBoot.file.exporter.factory.FileExporterFactory;
import com.example.projetoRestSpringBoot.model.Credencial;
import com.example.projetoRestSpringBoot.model.Curso;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.model.Treinamento;
import com.example.projetoRestSpringBoot.repository.CursoRepository;
import com.example.projetoRestSpringBoot.repository.FuncionarioRepository;
import com.example.projetoRestSpringBoot.repository.TreinamentoRepository;
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

import java.time.LocalDate;
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
    FileExporterFactory exporter;

    @Autowired
    CursoRepository cursoRepository;

    @Autowired(required = false)
    PagedResourcesAssembler<TreinamentoDTO> assembler;

    public PagedModel<EntityModel<TreinamentoDTO>> findAll(Pageable pageable) {
        logger.info(String.format("Procurando todos os treinamentos"));

        var people = repository.findAll(pageable);
        Page<TreinamentoDTO> peopleWithLinks = people.map(dto -> {
            TreinamentoDTO treinamento = parseObject(dto, TreinamentoDTO.class);
            if(dto.getFuncionario() != null){
                treinamento.setFuncionarioId(dto.getFuncionario().getId());
                treinamento.setFuncionarioNome(dto.getFuncionario().getNome());
                treinamento.setFuncionarioMatricula(dto.getFuncionario().getMatricula());
                treinamento.setCursoId(dto.getCurso().getId());
                treinamento.setCursoNome(dto.getCurso().getNome());
            }
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

        if(entity.getFuncionario() != null){
            dto.setFuncionarioId(entity.getFuncionario().getId());
            dto.setFuncionarioNome(entity.getFuncionario().getNome());
            dto.setFuncionarioMatricula(entity.getFuncionario().getMatricula());
            dto.setCursoId(entity.getCurso().getId());
            dto.setCursoNome(entity.getCurso().getNome());
        }
        addHateosLinks(dto);
        return dto;
    }

    public PagedModel<EntityModel<TreinamentoDTO>> findByInstrutor(
            String instrutor, Pageable pageable) {

        logger.info("Procurando treinamentos por status");

        // Busca a página de entidades
        Page<Treinamento> treinamentoPage = repository.findByInstrutor(instrutor, pageable);

        // Converte para DTO e adiciona links
        Page<TreinamentoDTO> funcionariosDTOPage = treinamentoPage.map(treinamento -> {
            TreinamentoDTO dto = parseObject(treinamento, TreinamentoDTO.class);
            addHateosLinks(dto);
            return dto;
        });

        // Cria link de página raiz
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(TreinamentoController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();

        // Converte para PagedModel
        return assembler.toModel(funcionariosDTOPage, findAllLink);
    }

    public PagedModel<EntityModel<TreinamentoDTO>> findByStatus(
            TreinamentoStatus status, Pageable pageable) {

        logger.info("Procurando treinamentos por status");

        // Busca a página de entidades
        Page<Treinamento> treinamentoPage = repository.findByStatus(status, pageable);

        // Converte para DTO e adiciona links
        Page<TreinamentoDTO> funcionariosDTOPage = treinamentoPage.map(treinamento -> {
            TreinamentoDTO dto = parseObject(treinamento, TreinamentoDTO.class);
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

    public PagedModel<EntityModel<TreinamentoDTO>> findTreinamentoExpiring(
            LocalDate startDate, LocalDate endDate, Pageable pageable) {

        logger.info("Procurando treinamentos expirando entre datas");

        // Busca a página de entidades
        Page<Treinamento> treinamentoPage = repository.findTreinamentoExpiring(startDate, endDate, pageable);

        // Converte para DTO e adiciona links
        Page<TreinamentoDTO> treinamentoPageDTO = treinamentoPage.map(treinamento -> {
            TreinamentoDTO dto = parseObject(treinamento, TreinamentoDTO.class);
            if(treinamento.getFuncionario() != null){
                dto.setFuncionarioId(treinamento.getFuncionario().getId());
                dto.setFuncionarioNome(treinamento.getFuncionario().getNome());
                dto.setFuncionarioMatricula(treinamento.getFuncionario().getMatricula());
                dto.setCursoId(treinamento.getCurso().getId());
                dto.setCursoNome(treinamento.getCurso().getNome());
            }
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
        return assembler.toModel(treinamentoPageDTO, findAllLink);
    }

    public PagedModel<EntityModel<TreinamentoDTO>> findTreinamentoConluded(
            LocalDate startDate, LocalDate endDate, Pageable pageable) {

        logger.info("Procurando treinamentos expirando entre datas");

        // Busca a página de entidades
        Page<Treinamento> treinamentoPage = repository.findTreinamentoConluded(startDate, endDate, pageable);

        // Converte para DTO e adiciona links
        Page<TreinamentoDTO> treinamentoPageDTO = treinamentoPage.map(treinamento -> {
            TreinamentoDTO dto = parseObject(treinamento, TreinamentoDTO.class);
            if(treinamento.getFuncionario() != null){
                dto.setFuncionarioId(treinamento.getFuncionario().getId());
                dto.setFuncionarioNome(treinamento.getFuncionario().getNome());
                dto.setFuncionarioMatricula(treinamento.getFuncionario().getMatricula());
                dto.setCursoId(treinamento.getCurso().getId());
                dto.setCursoNome(treinamento.getCurso().getNome());
            }
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
        return assembler.toModel(treinamentoPageDTO, findAllLink);
    }

    public PagedModel<EntityModel<TreinamentoDTO>> findTreinamentosByFuncionario(
            long id, Pageable pageable) {

        logger.info("Procurando todos os treinamentos de um funcionário especifico");

        // Busca a página de entidades
        Page<Treinamento> treinamentoPage = repository.findTreinamentosByFuncionario(id, pageable);

        // Converte para DTO e adiciona links
        Page<TreinamentoDTO> treinamentoPageDTO = treinamentoPage.map(credencial -> {
            TreinamentoDTO dto = parseObject(credencial, TreinamentoDTO.class);
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
        return assembler.toModel(treinamentoPageDTO, findAllLink);
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
        if(entity.getFuncionario() != null){
            dto.setFuncionarioId(entity.getFuncionario().getId());
            dto.setFuncionarioNome(entity.getFuncionario().getNome());
            dto.setFuncionarioMatricula(entity.getFuncionario().getMatricula());
            dto.setCursoId(entity.getCurso().getId());
            dto.setCursoNome(entity.getCurso().getNome());
        }
        addHateosLinks(dto);

        return dto;
    }


    public void delete(long id) {
        logger.info(String.format("Apagando um treinamento do banco"));

        Treinamento entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado no banco"));
        repository.delete(entity);
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Exportando a tabela de treinamentos no formato {}", acceptHeader);

        var treinamentos = repository.findAll(pageable)
                .map(treinamento -> {
                    TreinamentoDTO dto = parseObject(treinamento, TreinamentoDTO.class);

                    // Popula os dados do funcionário
                    if (treinamento.getFuncionario() != null) {
                        dto.setFuncionarioId(treinamento.getFuncionario().getId());
                        dto.setFuncionarioNome(treinamento.getFuncionario().getNome());
                        dto.setFuncionarioMatricula(treinamento.getFuncionario().getMatricula());
                    }

                    // Popula os dados do curso
                    if (treinamento.getCurso() != null) {
                        dto.setCursoId(treinamento.getCurso().getId());
                        dto.setCursoNome(treinamento.getCurso().getNome());
                    }

                    return dto;
                })
                .getContent();

        try {
            FileExporter exporterObj = this.exporter.getExporter(acceptHeader);
            return exporterObj.exportTreinamentos(treinamentos);
        } catch (Exception e) {
            logger.error("Erro ao exportar arquivo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao exportar o arquivo");
        }
    }



    private static void addHateosLinks(TreinamentoDTO dto) {
        dto.add(linkTo(methodOn(TreinamentoController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(TreinamentoController.class).delete(dto.getId())).withRel("delete").withType("GET"));
        dto.add(linkTo(methodOn(TreinamentoController.class).create(parseObject(dto, Treinamento.class))).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(TreinamentoController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(TreinamentoController.class).exportPage(1, 12, "asc", null)).withRel("exportPage").withType("GET"));
    }

}

