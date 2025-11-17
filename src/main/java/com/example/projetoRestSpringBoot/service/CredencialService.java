package com.example.projetoRestSpringBoot.service;

import com.example.projetoRestSpringBoot.controller.CredencialController;
import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.enums.CredencialStatus;
import com.example.projetoRestSpringBoot.exception.BadRequestException;
import com.example.projetoRestSpringBoot.exception.FileStorageException;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import com.example.projetoRestSpringBoot.file.exporter.factory.FileExporterFactory;
import com.example.projetoRestSpringBoot.model.Credencial;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.repository.CredencialRepository;
import com.example.projetoRestSpringBoot.repository.FuncionarioRepository;
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
import java.util.stream.Collectors;

import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CredencialService {

    private final Logger logger = LoggerFactory.getLogger(CredencialService.class.getName());

    @Autowired
    private CredencialRepository repository;

    @Autowired
    private FileExporterFactory exporter;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired(required = false)
    private PagedResourcesAssembler<CredencialDTO> assembler;

    public PagedModel<EntityModel<CredencialDTO>> findAll(Pageable pageable) {
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
            logger.info("Procurando todas as credenciais");
            var credenciais = repository.findAll(pageable);
            Page<CredencialDTO> credenciaisWithLinks = credenciais.map(dto -> {
                CredencialDTO credencial = parseObject(dto, CredencialDTO.class);

                if (dto.getFuncionario() != null) {
                    credencial.setFuncionarioId(dto.getFuncionario().getId());
                    credencial.setFuncionarioNome(dto.getFuncionario().getNome());
                    credencial.setFuncionarioMatricula(dto.getFuncionario().getMatricula());
                }

                HateoasLinkManager.addCredencialDetailLinks(credencial);
                return credencial;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(CredencialController.class).findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();
            var result = assembler.toModel(credenciaisWithLinks, findAllLink);
            HateoasLinkManager.addCredencialListPageLinks(result);
            logger.info("Total de credenciais encontradas: {}", credenciais.getTotalElements());
            return result;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar todas as credenciais: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar todas as credenciais: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar credenciais: " + e.getMessage());
        }
    }

    public CredencialDTO findById(long id) {
        if (id <= 0) {
            throw new BadRequestException("ID deve ser maior que zero");
        }

        try {
            logger.info("Procurando uma credencial pelo ID: {}", id);
            var entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Credencial não encontrada para o ID: " + id));

            var dto = parseObject(entity, CredencialDTO.class);

            if (entity.getFuncionario() != null) {
                dto.setFuncionarioId(entity.getFuncionario().getId());
                dto.setFuncionarioNome(entity.getFuncionario().getNome());
                dto.setFuncionarioMatricula(entity.getFuncionario().getMatricula());
            }

            HateoasLinkManager.addCredencialDetailLinks(dto);
            logger.info("Credencial encontrada: ID {}", id);
            return dto;
        } catch (ResourceNotFoundException e) {
            logger.warn("Credencial não encontrada: {}", e.getMessage());
            throw e;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar credencial por ID: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar credencial por ID: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar credencial: " + e.getMessage());
        }
    }

    public Credencial create(CredencialDTO credencialDTO) {
        if (credencialDTO == null) {
            throw new RequiredObjectIsNullException();
        }
        if (credencialDTO.getFuncionarioId() == null) {
            throw new BadRequestException("Funcionário é obrigatório");
        }
        if (credencialDTO.getTipo() == null) {
            throw new BadRequestException("Tipo da credencial é obrigatório");
        }
        if (credencialDTO.getDataEmissao() == null) {
            throw new BadRequestException("Data de emissão é obrigatória");
        }
        if (credencialDTO.getDataVencimento() == null) {
            throw new BadRequestException("Data de vencimento é obrigatória");
        }
        if (credencialDTO.getDataVencimento().isBefore(credencialDTO.getDataEmissao())) {
            throw new BadRequestException("Data de vencimento não pode ser anterior à data de emissão");
        }

        try {
            logger.info("Criando uma nova credencial no banco");
            Funcionario funcionario = funcionarioRepository.findById(credencialDTO.getFuncionarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado para o ID: " + credencialDTO.getFuncionarioId()));

            Credencial entity = new Credencial();
            entity.setTipo(credencialDTO.getTipo());
            entity.setFuncionario(funcionario);
            entity.setDataEmissao(credencialDTO.getDataEmissao());
            entity.setDataVencimento(credencialDTO.getDataVencimento());
            entity.setStatus(credencialDTO.getStatus() != null ? credencialDTO.getStatus() : calcularStatus(credencialDTO.getDataVencimento()));

            var savedEntity = repository.save(entity);
            logger.info("Credencial criada com sucesso: ID {}", savedEntity.getId());
            return savedEntity;
        } catch (BadRequestException | RequiredObjectIsNullException | ResourceNotFoundException e) {
            logger.warn("Erro de validação ao criar credencial: {}", e.getMessage());
            throw e;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao criar credencial: {}", e.getMessage(), e);
            throw new BadRequestException("Credencial duplicada ou dados inválidos");
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar credencial: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar credencial: " + e.getMessage());
        }
    }


    public CredencialDTO update(CredencialDTO credencial) {
        if (credencial == null) {
            throw new RequiredObjectIsNullException();
        }
        if (credencial.getId() == null || credencial.getId() <= 0) {
            throw new BadRequestException("ID da credencial inválido ou ausente");
        }
        if (credencial.getTipo() == null) {
            throw new BadRequestException("Tipo da credencial é obrigatório");
        }
        if (credencial.getDataEmissao() == null) {
            throw new BadRequestException("Data de emissão é obrigatória");
        }
        if (credencial.getDataVencimento() == null) {
            throw new BadRequestException("Data de vencimento é obrigatória");
        }
        if (credencial.getDataVencimento().isBefore(credencial.getDataEmissao())) {
            throw new BadRequestException("Data de vencimento não pode ser anterior à data de emissão");
        }

        try {
            logger.info("Atualizando uma credencial no banco: ID {}", credencial.getId());
            Credencial entity = repository.findById(credencial.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Credencial não encontrada para o ID: " + credencial.getId()));

            entity.setTipo(credencial.getTipo());
            entity.setDataEmissao(credencial.getDataEmissao());
            entity.setDataVencimento(credencial.getDataVencimento());
            entity.setStatus(credencial.getStatus() != null ? credencial.getStatus() : calcularStatus(credencial.getDataVencimento()));

            if (credencial.getFuncionarioId() != null) {
                Funcionario funcionario = funcionarioRepository.findById(credencial.getFuncionarioId())
                        .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado para o ID: " + credencial.getFuncionarioId()));
                entity.setFuncionario(funcionario);
            }

            var savedEntity = repository.save(entity);
            var dto = parseObject(savedEntity, CredencialDTO.class);

            if (savedEntity.getFuncionario() != null) {
                dto.setFuncionarioId(savedEntity.getFuncionario().getId());
                dto.setFuncionarioNome(savedEntity.getFuncionario().getNome());
                dto.setFuncionarioMatricula(savedEntity.getFuncionario().getMatricula());
            }
            HateoasLinkManager.addCredencialDetailLinks(dto);
            logger.info("Credencial atualizada com sucesso: ID {}", credencial.getId());
            return dto;
        } catch (BadRequestException | ResourceNotFoundException | RequiredObjectIsNullException e) {
            logger.warn("Erro de validação ao atualizar credencial: {}", e.getMessage());
            throw e;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao atualizar credencial: {}", e.getMessage(), e);
            throw new BadRequestException("Erro ao atualizar: dados duplicados ou inválidos");
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar credencial: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar credencial: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<CredencialDTO>> findByStatus(CredencialStatus status, Pageable pageable) {
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
            logger.info("Procurando credenciais pelo status: {}", status);
            Page<Credencial> credencialPage = repository.findByStatus(status, pageable);
            Page<CredencialDTO> credencialDTOPage = credencialPage.map(credencial -> {
                CredencialDTO dto = parseObject(credencial, CredencialDTO.class);

                if (credencial.getFuncionario() != null) {
                    dto.setFuncionarioId(credencial.getFuncionario().getId());
                    dto.setFuncionarioNome(credencial.getFuncionario().getNome());
                    dto.setFuncionarioMatricula(credencial.getFuncionario().getMatricula());
                }

                HateoasLinkManager.addCredencialDetailLinks(dto);
                return dto;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(CredencialController.class).findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var result = assembler.toModel(credencialDTOPage, findAllLink);
            HateoasLinkManager.addCredencialListPageLinks(result);
            logger.info("Total de credenciais encontradas para status '{}': {}", status, credencialPage.getTotalElements());
            return result;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar credenciais por status: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar credenciais por status: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar credenciais por status: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<CredencialDTO>> findCredencialEmited(LocalDate startDate, LocalDate endDate, Pageable pageable) {
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
            logger.info("Procurando credenciais por data de emissão: {} a {}", startDate, endDate);
            Page<Credencial> credencialPage = repository.findCredencialEmited(startDate, endDate, pageable);
            Page<CredencialDTO> credencialDTOPage = credencialPage.map(credencial -> {
                CredencialDTO dto = parseObject(credencial, CredencialDTO.class);

                if (credencial.getFuncionario() != null) {
                    dto.setFuncionarioId(credencial.getFuncionario().getId());
                    dto.setFuncionarioNome(credencial.getFuncionario().getNome());
                    dto.setFuncionarioMatricula(credencial.getFuncionario().getMatricula());
                }

                HateoasLinkManager.addCredencialDetailLinks(dto);
                return dto;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(CredencialController.class).findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var result = assembler.toModel(credencialDTOPage, findAllLink);
            HateoasLinkManager.addCredencialListPageLinks(result);
            logger.info("Total de credenciais emitidas entre {} e {}: {}", startDate, endDate, credencialPage.getTotalElements());
            return result;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar credenciais por data de emissão: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar credenciais por data de emissão: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar credenciais por data de emissão: " + e.getMessage());
        }
    }

    public PagedModel<EntityModel<CredencialDTO>> findCredencialExpiring(LocalDate startDate, LocalDate endDate, Pageable pageable) {
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
            logger.info("Procurando credenciais por data de vencimento: {} a {}", startDate, endDate);
            Page<Credencial> credencialPage = repository.findCredencialExpiring(startDate, endDate, pageable);
            Page<CredencialDTO> credencialDTOPage = credencialPage.map(credencial -> {
                CredencialDTO dto = parseObject(credencial, CredencialDTO.class);

                if (credencial.getFuncionario() != null) {
                    dto.setFuncionarioId(credencial.getFuncionario().getId());
                    dto.setFuncionarioNome(credencial.getFuncionario().getNome());
                    dto.setFuncionarioMatricula(credencial.getFuncionario().getMatricula());
                }

                HateoasLinkManager.addCredencialDetailLinks(dto);
                return dto;
            });

            Link findAllLink = WebMvcLinkBuilder.linkTo(
                    methodOn(CredencialController.class).findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                    )
            ).withSelfRel();

            var result = assembler.toModel(credencialDTOPage, findAllLink);
            HateoasLinkManager.addCredencialListPageLinks(result);
            logger.info("Total de credenciais vencendo entre {} e {}: {}", startDate, endDate, credencialPage.getTotalElements());
            return result;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao buscar credenciais por data de vencimento: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar credenciais por data de vencimento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar credenciais por data de vencimento: " + e.getMessage());
        }
    }

    public void delete(long id) {
        if (id <= 0) {
            throw new BadRequestException("ID deve ser maior que zero");
        }

        try {
            logger.info("Apagando uma credencial do banco: ID {}", id);
            Credencial entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Credencial não encontrada para o ID: " + id));
            repository.delete(entity);
            logger.info("Credencial deletada com sucesso: ID {}", id);
        } catch (ResourceNotFoundException e) {
            logger.warn("Credencial não encontrada para deletar: {}", e.getMessage());
            throw e;
        } catch (BadRequestException e) {
            logger.warn("Erro de validação ao deletar credencial: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar credencial: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao deletar credencial: " + e.getMessage());
        }
    }

    public Resource exportPage(String acceptHeader) {
        if (acceptHeader == null || acceptHeader.trim().isEmpty()) {
            throw new BadRequestException("Header Accept é obrigatório");
        }

        try {
            logger.info("Exportando credenciais no formato: {}", acceptHeader);

            var credenciais = repository.findAll().stream()
                    .map(credencial -> {
                        CredencialDTO dto = parseObject(credencial, CredencialDTO.class);

                        if (credencial.getFuncionario() != null) {
                            dto.setFuncionarioId(credencial.getFuncionario().getId());
                            dto.setFuncionarioNome(credencial.getFuncionario().getNome());
                            dto.setFuncionarioMatricula(credencial.getFuncionario().getMatricula());
                        }

                        return dto;
                    })
                    .collect(Collectors.toList());

            if (credenciais.isEmpty()) {
                throw new ResourceNotFoundException("Nenhuma credencial encontrada para exportar");
            }

            FileExporter exporter = this.exporter.getExporter(acceptHeader);
            var resource = exporter.exportarCredenciais(credenciais);
            logger.info("Credenciais exportadas com sucesso");
            return resource;
        } catch (BadRequestException | ResourceNotFoundException e) {
            logger.warn("Erro ao exportar credenciais: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Formato de exportação inválido: {}", e.getMessage(), e);
            throw new BadRequestException("Formato de arquivo não suportado: " + acceptHeader);
        } catch (Exception e) {
            logger.error("Erro inesperado ao exportar arquivo: {}", e.getMessage(), e);
            throw new FileStorageException("Erro ao exportar o arquivo: " + e.getMessage(), e);
        }
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void atualizarStatusCredenciais() {
        try {
            logger.info("Iniciando atualização automática de status das credenciais");
            List<Credencial> credenciais = repository.findAll();
            for (Credencial c : credenciais) {
                c.setStatus(calcularStatus(c.getDataVencimento()));
            }
            repository.saveAll(credenciais);
            logger.info("Status de {} credenciais atualizados com sucesso", credenciais.size());
        } catch (Exception e) {
            logger.error("Erro ao atualizar status das credenciais: {}", e.getMessage(), e);
        }
    }

    private CredencialStatus calcularStatus(LocalDate dataVencimento) {
        LocalDate hoje = LocalDate.now();
        if (hoje.isAfter(dataVencimento)) {
            return CredencialStatus.VENCIDA;
        } else if (hoje.plusDays(90).isAfter(dataVencimento)) {
            return CredencialStatus.VENCIMENTO_PROXIMO;
        } else {
            return CredencialStatus.VALIDA;
        }
    }
}
