package com.example.projetoRestSpringBoot.service;

import com.example.projetoRestSpringBoot.controller.FuncionarioController;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.enums.FuncionarioSituacao;
import com.example.projetoRestSpringBoot.exception.BadRequestException;
import com.example.projetoRestSpringBoot.exception.FileStorageException;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
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


import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;
import static com.example.projetoRestSpringBoot.service.linkhateoas.HateoasLinkManager.addFuncionarioDetailLinks;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class FuncionarioService {

    private final AtomicLong counter = new AtomicLong();
    private final Logger logger = LoggerFactory.getLogger(FuncionarioService.class.getName());

    @Autowired
    private FuncionarioRepository repository;

    @Autowired
    private FileImporterFactory importer;

    @Autowired
    private FileExporterFactory exporter;

    @Autowired(required = false)
    private PagedResourcesAssembler<FuncionarioDTO> assembler;

    public PagedModel<EntityModel<FuncionarioDTO>> findAll(Pageable pageable) {
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
            logger.info("Procurando todos os funcionarios");

            var people = repository.findAll(pageable);
            Page<FuncionarioDTO> peopleWithLinks = people.map(dto -> {
                FuncionarioDTO person = parseObject(dto, FuncionarioDTO.class);
                addFuncionarioDetailLinks(person);
                return person;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(FuncionarioController.class).findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var pagedModel = assembler.toModel(peopleWithLinks, findAllLink);
            logger.info("Total de funcionarios encontrados: {}", people.getTotalElements());
            return pagedModel;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar todos os funcionarios: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar todos os funcionarios: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar funcionarios: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<FuncionarioDTO>> findByName(String nome, Pageable pageable) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new BadRequestException("Nome não pode estar vazio");
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
            logger.info("Procurando funcionarios pelo nome: {}", nome);

            var funcionarios = repository.findFuncionarioByName(nome, pageable);
            var funcionariosLinks = funcionarios.map(dto -> {
                var funcionario = parseObject(dto, FuncionarioDTO.class);
                addFuncionarioDetailLinks(funcionario);
                return funcionario;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(FuncionarioController.class).findByName(
                            nome,
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var pagedModel = assembler.toModel(funcionariosLinks, findAllLink);
            logger.info("Total de funcionarios encontrados para nome '{}': {}", nome, funcionarios.getTotalElements());
            return pagedModel;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar funcionario por nome: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar funcionario por nome: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar funcionario por nome: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<FuncionarioDTO>> findByNome(String nome, Pageable pageable) {
        return findByName(nome, pageable);
    }

    public FuncionarioDTO findById(long id) {
        if (id <= 0) {
            throw new BadRequestException("ID deve ser maior que zero");
        }

        try {
            logger.info("Procurando funcionario pelo Id: {}", id);

            var entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado no banco"));

            var dto = parseObject(entity, FuncionarioDTO.class);
            addFuncionarioDetailLinks(dto);
            logger.info("Funcionario encontrado: ID {}", id);
            return dto;
        } catch (ResourceNotFoundException e) {
            logger.warn("Funcionario não encontrado: {}", e.getMessage());
            throw e;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar funcionario por ID: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar funcionario por ID: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar funcionario: " + e.getMessage());
        }
    }

    public FuncionarioDTO findByMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new BadRequestException("Matrícula não pode estar vazia");
        }

        try {
            logger.info("Procurando funcionario pela matricula: {}", matricula);

            var entity = repository.findByMatricula(matricula)
                    .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada no banco"));

            var dto = parseObject(entity, FuncionarioDTO.class);
            addFuncionarioDetailLinks(dto);
            logger.info("Funcionario encontrado pela matrícula: {}", matricula);
            return dto;
        } catch (ResourceNotFoundException e) {
            logger.warn("Funcionario não encontrado pela matrícula: {}", e.getMessage());
            throw e;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar funcionario por matrícula: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar funcionario por matrícula: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar funcionario: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<FuncionarioDTO>> findBySituacao(
            FuncionarioSituacao situacao, Pageable pageable) {

        if (situacao == null) {
            throw new BadRequestException("Situação não pode estar vazia");
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
            logger.info("Procurando funcionarios pela situação: {}", situacao);

            Page<Funcionario> funcionariosPage = repository.findBySituacao(situacao, pageable);

            Page<FuncionarioDTO> funcionariosDTOPage = funcionariosPage.map(funcionario -> {
                FuncionarioDTO dto = parseObject(funcionario, FuncionarioDTO.class);
                addFuncionarioDetailLinks(dto);
                return dto;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(FuncionarioController.class).findBySituacao(
                            situacao,
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var pagedModel = assembler.toModel(funcionariosDTOPage, findAllLink);
            logger.info("Total de funcionarios encontrados para situação '{}': {}", situacao, funcionariosPage.getTotalElements());
            return pagedModel;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar funcionarios por situação: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar funcionarios por situação: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar funcionarios: " + e.getMessage());
        }
    }

    public Funcionario create(Funcionario funcionario) {
        if (funcionario == null) {
            throw new RequiredObjectIsNullException();
        }
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new BadRequestException("Nome do funcionário é obrigatório");
        }
        if (funcionario.getMatricula() == null || funcionario.getMatricula().trim().isEmpty()) {
            throw new BadRequestException("Matrícula do funcionário é obrigatória");
        }

        try {
            logger.info("Criando um novo funcionario no banco");

            var savedEntity = repository.save(funcionario);
            logger.info("Funcionario criado com sucesso: ID {}", savedEntity.getId());
            return savedEntity;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao criar funcionario: {}", e.getMessage());
            throw e;
        } catch (RequiredObjectIsNullException e) {
            logger.warn("Objeto nulo ao criar funcionario: {}", e.getMessage());
            throw e;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao criar funcionario: {}", e.getMessage(), e);
            throw new BadRequestException("Funcionário com matrícula duplicada ou dados inválidos");
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar funcionario: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar funcionario: " + e.getMessage());
        }
    }

    public FuncionarioDTO update(FuncionarioDTO funcionario) {
        if (funcionario == null) {
            throw new RequiredObjectIsNullException();
        }
        if (funcionario.getId() == null || funcionario.getId() <= 0) {
            throw new BadRequestException("ID do funcionário inválido ou ausente");
        }
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new BadRequestException("Nome do funcionário é obrigatório");
        }
        if (funcionario.getMatricula() == null || funcionario.getMatricula().trim().isEmpty()) {
            throw new BadRequestException("Matrícula do funcionário é obrigatória");
        }

        try {
            logger.info("Atualizando funcionario: ID {}", funcionario.getId());

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

            var savedEntity = repository.save(entity);
            var dto = parseObject(savedEntity, FuncionarioDTO.class);
            addFuncionarioDetailLinks(dto);
            logger.info("Funcionario atualizado com sucesso: ID {}", funcionario.getId());
            return dto;
        } catch (BadRequestException | ResourceNotFoundException | RequiredObjectIsNullException e) {
            logger.warn("Erro de validação ao atualizar funcionario: {}", e.getMessage());
            throw e;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao atualizar funcionario: {}", e.getMessage(), e);
            throw new BadRequestException("Erro ao atualizar: matrícula duplicada ou dados inválidos");
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar funcionario: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar funcionario: " + e.getMessage());
        }
    }

    public Resource exportPage(String acceptHeader) {
        if (acceptHeader == null || acceptHeader.trim().isEmpty()) {
            throw new BadRequestException("Header Accept é obrigatório");
        }

        try {
            logger.info("Exportando funcionarios no formato: {}", acceptHeader);

            var funcionarios = repository.findAll().stream()
                    .map(funcionario -> parseObject(funcionario, FuncionarioDTO.class))
                    .collect(Collectors.toList());

            if (funcionarios.isEmpty()) {
                throw new ResourceNotFoundException("Nenhum funcionário encontrado para exportar");
            }

            FileExporter exporter = this.exporter.getExporter(acceptHeader);
            var resource = exporter.exportarFuncionarios(funcionarios);
            logger.info("Funcionarios exportados com sucesso");
            return resource;
        } catch (BadRequestException | ResourceNotFoundException e) {
            logger.warn("Erro ao exportar funcionarios: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Formato de exportação inválido: {}", e.getMessage(), e);
            throw new BadRequestException("Formato de arquivo não suportado: " + acceptHeader);
        } catch (Exception e) {
            logger.error("Erro inesperado ao exportar arquivo: {}", e.getMessage(), e);
            throw new FileStorageException("Erro ao exportar o arquivo: " + e.getMessage(), e);
        }
    }

    public PagedModel<EntityModel<FuncionarioDTO>> findFuncionarioByAddmitedDate(
            LocalDate startDate, LocalDate endDate, Pageable pageable) {

        if (startDate == null || endDate == null) {
            throw new BadRequestException("Data de início e fim são obrigatórias");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Data de início não pode ser posterior à data de fim");
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
            logger.info("Procurando funcionarios por data de admissão: {} a {}", startDate, endDate);

            Page<Funcionario> funcionariosPage = repository.findFuncionarioByAddmitedDate(startDate, endDate, pageable);

            Page<FuncionarioDTO> funcionariosDTOPage = funcionariosPage.map(funcionario -> {
                FuncionarioDTO dto = parseObject(funcionario, FuncionarioDTO.class);
                addFuncionarioDetailLinks(dto);
                return dto;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(FuncionarioController.class).findByAdmissao(
                            null,
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var pagedModel = assembler.toModel(funcionariosDTOPage, findAllLink);
            logger.info("Total de funcionarios encontrados para data: {}", funcionariosPage.getTotalElements());
            return pagedModel;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar funcionarios por data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar funcionarios por data: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar funcionarios: " + e.getMessage());
        }
    }

    public void delete(long id) {
        if (id <= 0) {
            throw new BadRequestException("ID deve ser maior que zero");
        }

        try {
            logger.info("Deletando funcionario: ID {}", id);

            Funcionario entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado no banco"));

            repository.delete(entity);
            logger.info("Funcionario deletado com sucesso: ID {}", id);
        } catch (ResourceNotFoundException e) {
            logger.warn("Funcionario não encontrado para deletar: {}", e.getMessage());
            throw e;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao deletar funcionario: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar funcionario: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao deletar funcionario: " + e.getMessage());
        }
    }

    public List<FuncionarioDTO> importarArquivo(MultipartFile file) {
        if (file == null) {
            throw new BadRequestException("Arquivo não pode ser nulo");
        }
        if (file.isEmpty()) {
            throw new BadRequestException("Arquivo vazio");
        }

        String fileName = Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(() -> new BadRequestException("Nome do arquivo ausente"));

        if (fileName.trim().isEmpty()) {
            throw new BadRequestException("Nome do arquivo inválido");
        }

        try {
            logger.info("Importando funcionarios a partir do arquivo: {}", fileName);

            try (InputStream inputStream = file.getInputStream()) {
                FileImporter importer = this.importer.getImporter(fileName);

                List<Funcionario> entities = importer.importarFuncionarios(inputStream).stream()
                        .map(dto -> {
                            try {
                                return repository.save(parseObject(dto, Funcionario.class));
                            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                                logger.error("Erro de integridade ao salvar funcionario do arquivo: {}", e.getMessage());
                                throw new BadRequestException("Dados duplicados ou inválidos no arquivo");
                            }
                        })
                        .toList();

                var result = entities.stream()
                        .map(entity -> {
                            var dto = parseObject(entity, FuncionarioDTO.class);
                            addFuncionarioDetailLinks(dto);
                            return dto;
                        })
                        .toList();

                logger.info("Arquivo importado com sucesso: {} funcionarios", result.size());
                return result;
            }
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao importar arquivo: {}", e.getMessage());
            throw e;
        } catch (java.io.IOException e) {
            logger.error("Erro ao ler arquivo: {}", e.getMessage(), e);
            throw new FileStorageException("Erro ao ler o arquivo: " + e.getMessage(), e);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao importar funcionarios: {}", e.getMessage(), e);
            throw new BadRequestException("Erro ao importar: dados duplicados ou inválidos no arquivo");
        } catch (IllegalArgumentException e) {
            logger.error("Tipo de arquivo não suportado: {}", e.getMessage(), e);
            throw new BadRequestException("Tipo de arquivo não suportado: " + fileName);
        } catch (Exception e) {
            logger.error("Erro inesperado ao importar arquivo: {}", e.getMessage(), e);
            throw new FileStorageException("Erro ao processar o arquivo: " + e.getMessage(), e);
        }
    }
}
