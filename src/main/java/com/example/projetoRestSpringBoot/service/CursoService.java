package com.example.projetoRestSpringBoot.service;

import com.example.projetoRestSpringBoot.controller.CursoController;
import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.exception.BadRequestException;
import com.example.projetoRestSpringBoot.exception.FileStorageException;
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
    private final Logger logger = LoggerFactory.getLogger(CursoService.class.getName());

    @Autowired
    private CursoRepository repository;

    @Autowired
    private FileImporterFactory importer;

    @Autowired
    private FileExporterFactory exporter;

    @Autowired(required = false)
    private PagedResourcesAssembler<CursoDTO> assembler;

    public PagedModel<EntityModel<CursoDTO>> findAll(Pageable pageable) {
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
            logger.info("Procurando todos os cursos");
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
            var pagedModel = assembler.toModel(peopleWithLinks, findAllLink);
            logger.info("Total de cursos encontrados: {}", people.getTotalElements());
            return pagedModel;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar todos os cursos: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar todos os cursos: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar cursos: " + e.getMessage());
        }
    }

    public CursoDTO findById(long id) {
        if (id <= 0) {
            throw new BadRequestException("ID deve ser maior que zero");
        }

        try {
            logger.info("Procurando um curso pelo Id: {}", id);
            var entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado para o ID: " + id));
            var dto = parseObject(entity, CursoDTO.class);
            HateoasLinkManager.addCursoDetailLinks(dto);
            logger.info("Curso encontrado: ID {}", id);
            return dto;
        } catch (ResourceNotFoundException e) {
            logger.warn("Curso não encontrado: {}", e.getMessage());
            throw e;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar curso por ID: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar curso por ID: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar curso: " + e.getMessage());
        }
    }

    public Curso create(Curso curso) {
        if (curso == null) {
            throw new RequiredObjectIsNullException();
        }
        if (curso.getNome() == null || curso.getNome().trim().isEmpty()) {
            throw new BadRequestException("Nome do curso é obrigatório");
        }

        try {
            logger.info("Criando um novo curso no banco");
            var entity = parseObject(curso, Curso.class);
            var savedEntity = repository.save(entity);
            var dto = parseObject(savedEntity, CursoDTO.class);
            HateoasLinkManager.addCursoDetailLinks(dto);
            logger.info("Curso criado com sucesso: ID {}", savedEntity.getId());
            return savedEntity;
        } catch (BadRequestException | RequiredObjectIsNullException e) {
            logger.warn("Erro de validação ao criar curso: {}", e.getMessage());
            throw e;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao criar curso: {}", e.getMessage(), e);
            throw new BadRequestException("Curso com nome duplicado ou dados inválidos");
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar curso: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar curso: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<CursoDTO>> findByName(String nome, Pageable pageable) {
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
            logger.info("Procurando curso(s) pelo nome: {}", nome);
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
            var pagedModel = assembler.toModel(cursosLink, findAllLink);
            logger.info("Total de cursos encontrados para nome '{}': {}", nome, cursos.getTotalElements());
            return pagedModel;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar curso por nome: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar curso por nome: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar curso por nome: " + e.getMessage());
        }
    }

    public CursoDTO update(CursoDTO curso) {
        if (curso == null) {
            throw new RequiredObjectIsNullException();
        }
        if (curso.getId() == null || curso.getId() <= 0) {
            throw new BadRequestException("ID do curso inválido ou ausente");
        }
        if (curso.getNome() == null || curso.getNome().trim().isEmpty()) {
            throw new BadRequestException("Nome do curso é obrigatório");
        }

        try {
            logger.info("Atualizando um curso no banco: ID {}", curso.getId());
            Curso entity = repository.findById(curso.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado para o ID: " + curso.getId()));

            entity.setNome(curso.getNome());
            entity.setDescricao(curso.getDescricao());
            entity.setCargaHoraria(curso.getCargaHoraria());
            entity.setValidadeMeses(curso.getValidadeMeses());
            entity.setOrigemCurso(curso.getOrigemCurso());
            entity.setTipoObrigatoriedade(curso.getTipoObrigatoriedade());

            var savedEntity = repository.save(entity);
            var dto = parseObject(savedEntity, CursoDTO.class);
            HateoasLinkManager.addCursoDetailLinks(dto);
            logger.info("Curso atualizado com sucesso: ID {}", curso.getId());
            return dto;
        } catch (BadRequestException | ResourceNotFoundException | RequiredObjectIsNullException e) {
            logger.warn("Erro de validação ao atualizar curso: {}", e.getMessage());
            throw e;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao atualizar curso: {}", e.getMessage(), e);
            throw new BadRequestException("Erro ao atualizar: nome duplicado ou dados inválidos");
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar curso: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar curso: " + e.getMessage());
        }
    }

    public void delete(long id) {
        if (id <= 0) {
            throw new BadRequestException("ID deve ser maior que zero");
        }

        try {
            logger.info("Apagando um curso do banco: ID {}", id);
            Curso entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado para o ID: " + id));
            repository.delete(entity);
            logger.info("Curso deletado com sucesso: ID {}", id);
        } catch (ResourceNotFoundException e) {
            logger.warn("Curso não encontrado para deletar: {}", e.getMessage());
            throw e;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao deletar curso: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar curso: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao deletar curso: " + e.getMessage());
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
            logger.info("Exportando a tabela de cursos no formato: {}", acceptHeader);
            var cursos = repository.findAll(pageable)
                    .map(curso -> parseObject(curso, CursoDTO.class))
                    .getContent();

            if (cursos.isEmpty()) {
                throw new ResourceNotFoundException("Nenhum curso encontrado para exportação");
            }

            FileExporter exporter = this.exporter.getExporter(acceptHeader);
            var resource = exporter.exportCursos(cursos);
            logger.info("Cursos exportados com sucesso");
            return resource;
        } catch (BadRequestException | ResourceNotFoundException e) {
            logger.warn("Erro ao exportar cursos: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Formato de exportação inválido: {}", e.getMessage(), e);
            throw new BadRequestException("Formato de arquivo não suportado: " + acceptHeader);
        } catch (Exception e) {
            logger.error("Erro inesperado ao exportar arquivo: {}", e.getMessage(), e);
            throw new FileStorageException("Erro ao exportar o arquivo: " + e.getMessage(), e);
        }
    }

    public List<CursoDTO> importarArquivo(MultipartFile file) {
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
            logger.info("Importando cursos em massa a partir do arquivo: {}", fileName);

            try (InputStream inputStream = file.getInputStream()) {
                FileImporter importer = this.importer.getImporter(fileName);

                List<Curso> entities = importer.importarCursos(inputStream).stream()
                        .map(dto -> {
                            try {
                                return repository.save(parseObject(dto, Curso.class));
                            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                                logger.error("Erro de integridade ao salvar curso do arquivo: {}", e.getMessage());
                                throw new BadRequestException("Dados duplicados ou inválidos no arquivo");
                            }
                        })
                        .toList();

                var result = entities.stream()
                        .map(entity -> {
                            var dto = parseObject(entity, CursoDTO.class);
                            HateoasLinkManager.addCursoDetailLinks(dto);
                            return dto;
                        })
                        .toList();

                logger.info("Arquivo importado com sucesso: {} cursos", result.size());
                return result;
            }
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao importar arquivo: {}", e.getMessage());
            throw e;
        } catch (java.io.IOException e) {
            logger.error("Erro ao ler arquivo: {}", e.getMessage(), e);
            throw new FileStorageException("Erro ao ler o arquivo: " + e.getMessage(), e);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao importar cursos: {}", e.getMessage(), e);
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
