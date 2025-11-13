package com.example.projetoRestSpringBoot.service;

import com.example.projetoRestSpringBoot.controller.TreinamentoController;
import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import com.example.projetoRestSpringBoot.enums.TreinamentoStatus;
import com.example.projetoRestSpringBoot.exception.BadRequestException;
import com.example.projetoRestSpringBoot.exception.FileStorageException;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import com.example.projetoRestSpringBoot.file.exporter.MediaTypes;
import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import com.example.projetoRestSpringBoot.file.exporter.factory.FileExporterFactory;
import com.example.projetoRestSpringBoot.model.Curso;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.model.Treinamento;
import com.example.projetoRestSpringBoot.repository.CursoRepository;
import com.example.projetoRestSpringBoot.repository.FuncionarioRepository;
import com.example.projetoRestSpringBoot.repository.TreinamentoRepository;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TreinamentoService {

    private final Logger logger = LoggerFactory.getLogger(TreinamentoService.class.getName());

    @Autowired
    private TreinamentoRepository repository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private FileExporterFactory exporter;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired(required = false)
    private PagedResourcesAssembler<TreinamentoDTO> assembler;

    public PagedModel<EntityModel<TreinamentoDTO>> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new BadRequestException("Parâmetros de paginação não podem ser nulos");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new BadRequestException("Parâmetros de paginação inválidos: page >= 0 e size > 0");
        }
        if (assembler == null) {
            throw new RuntimeException("PagedResourcesAssembler não foi inicializado");
        }

        try {
            logger.info("Procurando todos os treinamentos");
            var people = repository.findAll(pageable);
            Page<TreinamentoDTO> peopleWithLinks = people.map(dto -> {
                TreinamentoDTO treinamento = parseObject(dto, TreinamentoDTO.class);
                if (dto.getFuncionario() != null) {
                    treinamento.setFuncionarioId(dto.getFuncionario().getId());
                    treinamento.setFuncionarioNome(dto.getFuncionario().getNome());
                    treinamento.setFuncionarioMatricula(dto.getFuncionario().getMatricula());
                    treinamento.setCursoId(dto.getCurso().getId());
                    treinamento.setCursoNome(dto.getCurso().getNome());
                }
                HateoasLinkManager.addTreinamentoDetailLinks(treinamento);
                return treinamento;
            });
            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(TreinamentoController.class).findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();
            var result = assembler.toModel(peopleWithLinks, findAllLink);
            HateoasLinkManager.addTreinamentoListPageLinks(result);
            logger.info("Total de treinamentos encontrados: {}", people.getTotalElements());
            return result;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar todos os treinamentos: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar todos os treinamentos: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar treinamentos: " + e.getMessage());
        }
    }

    public TreinamentoDTO findById(long id) {
        if (id <= 0) {
            throw new BadRequestException("ID deve ser maior que zero");
        }

        try {
            logger.info("Procurando um treinamento pelo ID: {}", id);
            var entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Treinamento não encontrado"));

            var dto = parseObject(entity, TreinamentoDTO.class);

            if (entity.getFuncionario() != null) {
                dto.setFuncionarioId(entity.getFuncionario().getId());
                dto.setFuncionarioNome(entity.getFuncionario().getNome());
                dto.setFuncionarioMatricula(entity.getFuncionario().getMatricula());
                dto.setCursoId(entity.getCurso().getId());
                dto.setCursoNome(entity.getCurso().getNome());
            }
            HateoasLinkManager.addTreinamentoDetailLinks(dto);
            logger.info("Treinamento encontrado: ID {}", id);
            return dto;
        } catch (ResourceNotFoundException e) {
            logger.warn("Treinamento não encontrado: {}", e.getMessage());
            throw e;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar treinamento por ID: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar treinamento por ID: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar treinamento: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<TreinamentoDTO>> findByInstrutor(String instrutor, Pageable pageable) {
        if (instrutor == null || instrutor.trim().isEmpty()) {
            throw new BadRequestException("Nome do instrutor não pode ser vazio");
        }
        if (pageable == null) {
            throw new BadRequestException("Parâmetros de paginação não podem ser nulos");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new BadRequestException("Parâmetros de paginação inválidos: page >= 0 e size > 0");
        }
        if (assembler == null) {
            throw new RuntimeException("PagedResourcesAssembler não foi inicializado");
        }

        try {
            logger.info("Procurando treinamentos por instrutor: {}", instrutor);
            Page<Treinamento> treinamentoPage = repository.findByInstrutor(instrutor, pageable);

            Page<TreinamentoDTO> funcionariosDTOPage = treinamentoPage.map(treinamento -> {
                TreinamentoDTO dto = parseObject(treinamento, TreinamentoDTO.class);

                if (treinamento.getFuncionario() != null) {
                    dto.setFuncionarioId(treinamento.getFuncionario().getId());
                    dto.setFuncionarioNome(treinamento.getFuncionario().getNome());
                    dto.setFuncionarioMatricula(treinamento.getFuncionario().getMatricula());
                    dto.setCursoId(treinamento.getCurso().getId());
                    dto.setCursoNome(treinamento.getCurso().getNome());
                }

                HateoasLinkManager.addTreinamentoDetailLinks(dto);
                return dto;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(TreinamentoController.class).findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var result = assembler.toModel(funcionariosDTOPage, findAllLink);
            HateoasLinkManager.addTreinamentoListPageLinks(result);
            logger.info("Total de treinamentos encontrados para instrutor '{}': {}", instrutor, treinamentoPage.getTotalElements());
            return result;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar treinamentos por instrutor: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar treinamentos por instrutor: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar treinamentos por instrutor: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<TreinamentoDTO>> findByStatus(TreinamentoStatus status, Pageable pageable) {
        if (status == null) {
            throw new BadRequestException("Status não pode ser nulo");
        }
        if (pageable == null) {
            throw new BadRequestException("Parâmetros de paginação não podem ser nulos");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new BadRequestException("Parâmetros de paginação inválidos: page >= 0 e size > 0");
        }
        if (assembler == null) {
            throw new RuntimeException("PagedResourcesAssembler não foi inicializado");
        }

        try {
            logger.info("Procurando treinamentos por status: {}", status);
            Page<Treinamento> treinamentoPage = repository.findByStatus(status, pageable);

            Page<TreinamentoDTO> funcionariosDTOPage = treinamentoPage.map(treinamento -> {
                TreinamentoDTO dto = parseObject(treinamento, TreinamentoDTO.class);
                if (treinamento.getFuncionario() != null) {
                    dto.setFuncionarioId(treinamento.getFuncionario().getId());
                    dto.setFuncionarioNome(treinamento.getFuncionario().getNome());
                    dto.setFuncionarioMatricula(treinamento.getFuncionario().getMatricula());
                    dto.setCursoId(treinamento.getCurso().getId());
                    dto.setCursoNome(treinamento.getCurso().getNome());
                }
                HateoasLinkManager.addTreinamentoDetailLinks(dto);
                return dto;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(TreinamentoController.class).findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var result = assembler.toModel(funcionariosDTOPage, findAllLink);
            HateoasLinkManager.addTreinamentoListPageLinks(result);
            logger.info("Total de treinamentos encontrados para status '{}': {}", status, treinamentoPage.getTotalElements());
            return result;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar treinamentos por status: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar treinamentos por status: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar treinamentos por status: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<TreinamentoDTO>> findTreinamentoExpiring(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Datas de início e fim são obrigatórias");
        }
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("Data final não pode ser anterior à data inicial");
        }
        if (pageable == null) {
            throw new BadRequestException("Parâmetros de paginação não podem ser nulos");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new BadRequestException("Parâmetros de paginação inválidos: page >= 0 e size > 0");
        }
        if (assembler == null) {
            throw new RuntimeException("PagedResourcesAssembler não foi inicializado");
        }

        try {
            logger.info("Procurando treinamentos expirando entre {} e {}", startDate, endDate);
            Page<Treinamento> treinamentoPage = repository.findTreinamentoExpiring(startDate, endDate, pageable);

            Page<TreinamentoDTO> treinamentoPageDTO = treinamentoPage.map(treinamento -> {
                TreinamentoDTO dto = parseObject(treinamento, TreinamentoDTO.class);
                if (treinamento.getFuncionario() != null) {
                    dto.setFuncionarioId(treinamento.getFuncionario().getId());
                    dto.setFuncionarioNome(treinamento.getFuncionario().getNome());
                    dto.setFuncionarioMatricula(treinamento.getFuncionario().getMatricula());
                    dto.setCursoId(treinamento.getCurso().getId());
                    dto.setCursoNome(treinamento.getCurso().getNome());
                }
                HateoasLinkManager.addTreinamentoDetailLinks(dto);
                return dto;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(TreinamentoController.class).findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var result = assembler.toModel(treinamentoPageDTO, findAllLink);
            HateoasLinkManager.addTreinamentoListPageLinks(result);
            logger.info("Total de treinamentos expirando entre {} e {}: {}", startDate, endDate, treinamentoPage.getTotalElements());
            return result;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar treinamentos expirando: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar treinamentos expirando: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar treinamentos expirando: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<TreinamentoDTO>> findTreinamentoConluded(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Datas de início e fim são obrigatórias");
        }
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("Data final não pode ser anterior à data inicial");
        }
        if (pageable == null) {
            throw new BadRequestException("Parâmetros de paginação não podem ser nulos");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new BadRequestException("Parâmetros de paginação inválidos: page >= 0 e size > 0");
        }
        if (assembler == null) {
            throw new RuntimeException("PagedResourcesAssembler não foi inicializado");
        }

        try {
            logger.info("Procurando treinamentos concluídos entre {} e {}", startDate, endDate);
            Page<Treinamento> treinamentoPage = repository.findTreinamentoConluded(startDate, endDate, pageable);

            Page<TreinamentoDTO> treinamentoPageDTO = treinamentoPage.map(treinamento -> {
                TreinamentoDTO dto = parseObject(treinamento, TreinamentoDTO.class);
                if (treinamento.getFuncionario() != null) {
                    dto.setFuncionarioId(treinamento.getFuncionario().getId());
                    dto.setFuncionarioNome(treinamento.getFuncionario().getNome());
                    dto.setFuncionarioMatricula(treinamento.getFuncionario().getMatricula());
                    dto.setCursoId(treinamento.getCurso().getId());
                    dto.setCursoNome(treinamento.getCurso().getNome());
                }
                HateoasLinkManager.addTreinamentoDetailLinks(dto);
                return dto;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(TreinamentoController.class).findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var result = assembler.toModel(treinamentoPageDTO, findAllLink);
            HateoasLinkManager.addTreinamentoListPageLinks(result);
            logger.info("Total de treinamentos concluídos entre {} e {}: {}", startDate, endDate, treinamentoPage.getTotalElements());
            return result;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar treinamentos concluídos: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar treinamentos concluídos: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar treinamentos concluídos: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<TreinamentoDTO>> findTreinamentosByFuncionario(long id, Pageable pageable) {
        if (id <= 0) {
            throw new BadRequestException("ID do funcionário deve ser maior que zero");
        }
        if (pageable == null) {
            throw new BadRequestException("Parâmetros de paginação não podem ser nulos");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new BadRequestException("Parâmetros de paginação inválidos: page >= 0 e size > 0");
        }
        if (assembler == null) {
            throw new RuntimeException("PagedResourcesAssembler não foi inicializado");
        }

        try {
            logger.info("Procurando todos os treinamentos do funcionário: {}", id);
            Page<Treinamento> treinamentoPage = repository.findTreinamentosByFuncionario(id, pageable);

            Page<TreinamentoDTO> treinamentoPageDTO = treinamentoPage.map(credencial -> {
                TreinamentoDTO dto = parseObject(credencial, TreinamentoDTO.class);
                if (credencial.getFuncionario() != null) {
                    dto.setFuncionarioId(credencial.getFuncionario().getId());
                    dto.setFuncionarioNome(credencial.getFuncionario().getNome());
                    dto.setFuncionarioMatricula(credencial.getFuncionario().getMatricula());
                    dto.setCursoId(credencial.getCurso().getId());
                    dto.setCursoNome(credencial.getCurso().getNome());
                }
                HateoasLinkManager.addTreinamentoDetailLinks(dto);
                return dto;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(TreinamentoController.class).findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var result = assembler.toModel(treinamentoPageDTO, findAllLink);
            HateoasLinkManager.addTreinamentoListPageLinks(result);
            logger.info("Total de treinamentos encontrados para funcionário {}: {}", id, treinamentoPage.getTotalElements());
            return result;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar treinamentos por funcionário: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar treinamentos por funcionário: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar treinamentos por funcionário: " + e.getMessage());
        }
    }

    public Treinamento create(Treinamento treinamento) {
        if (treinamento == null) {
            throw new RequiredObjectIsNullException();
        }
        if (treinamento.getFuncionario() == null || treinamento.getFuncionario().getId() == null) {
            throw new BadRequestException("Funcionário é obrigatório");
        }
        if (treinamento.getCurso() == null || treinamento.getCurso().getId() == null) {
            throw new BadRequestException("Curso é obrigatório");
        }
        if (treinamento.getDataAgendamento() == null) {
            throw new BadRequestException("Data de agendamento é obrigatória");
        }
        if (treinamento.getInstrutor() == null || treinamento.getInstrutor().trim().isEmpty()) {
            throw new BadRequestException("Instrutor é obrigatório");
        }

        try {
            logger.info("Criando um novo registro de treinamento no banco");
            Funcionario funcionario = funcionarioRepository.findById(treinamento.getFuncionario().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

            Curso curso = cursoRepository.findById(treinamento.getCurso().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

            Treinamento entity = new Treinamento();
            entity.setFuncionario(funcionario);
            entity.setCurso(curso);
            entity.setDataAgendamento(treinamento.getDataAgendamento());
            entity.setDataConcluido(treinamento.getDataConcluido());

            if (treinamento.getDataConcluido() != null) {
                entity.setDataVencimento(treinamento.getDataConcluido().plusMonths(curso.getValidadeMeses()));
            }

            entity.setInstrutor(treinamento.getInstrutor());
            entity.setStatus(treinamento.getStatus() != null ? treinamento.getStatus() : TreinamentoStatus.VALIDO);
            var savedEntity = repository.save(entity);
            logger.info("Treinamento criado com sucesso: ID {}", savedEntity.getId());
            return savedEntity;
        } catch (BadRequestException | RequiredObjectIsNullException | ResourceNotFoundException e) {
            logger.warn("Erro de validação ao criar treinamento: {}", e.getMessage());
            throw e;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao criar treinamento: {}", e.getMessage(), e);
            throw new BadRequestException("Treinamento duplicado ou dados inválidos");
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar treinamento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar treinamento: " + e.getMessage());
        }
    }

    public TreinamentoDTO update(TreinamentoDTO treinamentoDTO) {
        if (treinamentoDTO == null) {
            throw new RequiredObjectIsNullException();
        }
        if (treinamentoDTO.getId() == null || treinamentoDTO.getId() <= 0) {
            throw new BadRequestException("ID do treinamento inválido ou ausente");
        }
        if (treinamentoDTO.getInstrutor() == null || treinamentoDTO.getInstrutor().trim().isEmpty()) {
            throw new BadRequestException("Instrutor é obrigatório");
        }

        try {
            logger.info("Atualizando um treinamento no banco: ID {}", treinamentoDTO.getId());
            Treinamento entity = repository.findById(treinamentoDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Treinamento não encontrado no banco"));

            entity.setDataAgendamento(treinamentoDTO.getDataAgendamento());
            entity.setDataVencimento(treinamentoDTO.getDataVencimento());
            entity.setDataConcluido(treinamentoDTO.getDataConcluido());
            entity.setInstrutor(treinamentoDTO.getInstrutor());

            if (entity.getDataVencimento() != null) {
                entity.setStatus(calcularStatus(entity.getDataVencimento()));
            }

            var dto = parseObject(repository.save(entity), TreinamentoDTO.class);
            if (entity.getFuncionario() != null) {
                dto.setFuncionarioId(entity.getFuncionario().getId());
                dto.setFuncionarioNome(entity.getFuncionario().getNome());
                dto.setFuncionarioMatricula(entity.getFuncionario().getMatricula());
                dto.setCursoId(entity.getCurso().getId());
                dto.setCursoNome(entity.getCurso().getNome());
            }
            HateoasLinkManager.addTreinamentoDetailLinks(dto);
            logger.info("Treinamento atualizado com sucesso: ID {}", treinamentoDTO.getId());
            return dto;
        } catch (BadRequestException | ResourceNotFoundException | RequiredObjectIsNullException e) {
            logger.warn("Erro de validação ao atualizar treinamento: {}", e.getMessage());
            throw e;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao atualizar treinamento: {}", e.getMessage(), e);
            throw new BadRequestException("Erro ao atualizar: dados duplicados ou inválidos");
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar treinamento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar treinamento: " + e.getMessage());
        }
    }

    public void delete(long id) {
        if (id <= 0) {
            throw new BadRequestException("ID deve ser maior que zero");
        }

        try {
            logger.info("Apagando um treinamento do banco: ID {}", id);
            Treinamento entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Treinamento não encontrado no banco"));
            repository.delete(entity);
            logger.info("Treinamento deletado com sucesso: ID {}", id);
        } catch (ResourceNotFoundException e) {
            logger.warn("Treinamento não encontrado para deletar: {}", e.getMessage());
            throw e;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao deletar treinamento: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar treinamento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao deletar treinamento: " + e.getMessage());
        }
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        if (pageable == null) {
            throw new BadRequestException("Parâmetros de paginação não podem ser nulos");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new BadRequestException("Parâmetros de paginação inválidos: page >= 0 e size > 0");
        }
        if (acceptHeader == null || acceptHeader.trim().isEmpty()) {
            throw new BadRequestException("Header Accept é obrigatório");
        }

        try {
            logger.info("Exportando a tabela de treinamentos no formato: {}", acceptHeader);
            var treinamentos = repository.findAll(pageable)
                    .map(treinamento -> {
                        TreinamentoDTO dto = parseObject(treinamento, TreinamentoDTO.class);

                        if (treinamento.getFuncionario() != null) {
                            dto.setFuncionarioId(treinamento.getFuncionario().getId());
                            dto.setFuncionarioNome(treinamento.getFuncionario().getNome());
                            dto.setFuncionarioMatricula(treinamento.getFuncionario().getMatricula());
                        }

                        if (treinamento.getCurso() != null) {
                            dto.setCursoId(treinamento.getCurso().getId());
                            dto.setCursoNome(treinamento.getCurso().getNome());
                        }

                        return dto;
                    })
                    .getContent();

            if (treinamentos.isEmpty()) {
                throw new ResourceNotFoundException("Nenhum treinamento encontrado para exportar");
            }

            FileExporter exporterObj = this.exporter.getExporter(acceptHeader);
            var resource = exporterObj.exportTreinamentos(treinamentos);
            logger.info("Treinamentos exportados com sucesso");
            return resource;
        } catch (BadRequestException | ResourceNotFoundException e) {
            logger.warn("Erro ao exportar treinamentos: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Formato de exportação inválido: {}", e.getMessage(), e);
            throw new BadRequestException("Formato de arquivo não suportado: " + acceptHeader);
        } catch (Exception e) {
            logger.error("Erro inesperado ao exportar arquivo: {}", e.getMessage(), e);
            throw new FileStorageException("Erro ao exportar o arquivo: " + e.getMessage(), e);
        }
    }

    public Resource exportarPorId(long id) {
        if (id <= 0) {
            throw new BadRequestException("ID deve ser maior que zero");
        }

        try {
            logger.info("Exportando treinamento PDF por ID: {}", id);
            Treinamento treinamento = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Treinamento não encontrado"));

            TreinamentoDTO dto = parseObject(treinamento, TreinamentoDTO.class);

            if (treinamento.getFuncionario() != null) {
                dto.setFuncionarioId(treinamento.getFuncionario().getId());
                dto.setFuncionarioNome(treinamento.getFuncionario().getNome());
                dto.setFuncionarioMatricula(treinamento.getFuncionario().getMatricula());
            }

            if (treinamento.getCurso() != null) {
                dto.setCursoId(treinamento.getCurso().getId());
                dto.setCursoNome(treinamento.getCurso().getNome());
            }

            FileExporter exporterObj = this.exporter.getExporter(MediaTypes.APPLICATION_PDF_VALUE);
            var resource = exporterObj.exportTreinamentoPorId(dto, treinamento.getFuncionario().getId());
            logger.info("Treinamento exportado com sucesso: ID {}", id);
            return resource;
        } catch (ResourceNotFoundException e) {
            logger.warn("Treinamento não encontrado para exportar: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao exportar treinamento: {}", e.getMessage(), e);
            throw new FileStorageException("Erro ao exportar PDF: " + e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void atualizarStatusTreinamentos() {
        try {
            logger.info("Iniciando atualização automática de status dos treinamentos");
            List<Treinamento> treinamentos = repository.findAll();
            for (Treinamento t : treinamentos) {
                if (t.getDataVencimento() != null) {
                    t.setStatus(calcularStatus(t.getDataVencimento()));
                }
            }
            repository.saveAll(treinamentos);
            logger.info("Status de {} treinamentos atualizados com sucesso", treinamentos.size());
        } catch (Exception e) {
            logger.error("Erro ao atualizar status dos treinamentos: {}", e.getMessage(), e);
        }
    }

    private TreinamentoStatus calcularStatus(LocalDate dataVencimento) {
        LocalDate hoje = LocalDate.now();
        if (hoje.isAfter(dataVencimento)) {
            return TreinamentoStatus.VENCIDO;
        } else if (hoje.plusDays(90).isAfter(dataVencimento)) {
            return TreinamentoStatus.VENCIMENTO_PROXIMO;
        } else {
            return TreinamentoStatus.VALIDO;
        }
    }

}
