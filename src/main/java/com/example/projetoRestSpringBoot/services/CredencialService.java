package com.example.projetoRestSpringBoot.services;

import com.example.projetoRestSpringBoot.controller.CredencialController;
import com.example.projetoRestSpringBoot.controller.FuncionarioController;
import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.enums.CredencialStatus;
import com.example.projetoRestSpringBoot.enums.FuncionarioSituacao;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.exception.ResourceNotFoundException;
import com.example.projetoRestSpringBoot.model.Credencial;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.repository.CredencialRepository;
import com.example.projetoRestSpringBoot.repository.FuncionarioRepository;
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

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
public class CredencialService {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = LoggerFactory.getLogger(CredencialService.class.getName());
    @Autowired
    CredencialRepository repository;

    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired(required = false)
    PagedResourcesAssembler<CredencialDTO> assembler;

    public PagedModel<EntityModel<CredencialDTO>> findAll(Pageable pageable) {
        logger.info(String.format("Procurando todos os credenciais"));

        var credenciais = repository.findAll(pageable);
        Page<CredencialDTO> credenciaisWithLinks = credenciais.map(dto -> {
            CredencialDTO credencial = parseObject(dto, CredencialDTO.class);

            // Preenchendo os campos do funcionário
            if(dto.getFuncionario() != null){
                credencial.setFuncionarioId(dto.getFuncionario().getId());
                credencial.setFuncionarioNome(dto.getFuncionario().getNome());
                credencial.setFuncionarioMatricula(dto.getFuncionario().getMatricula());
            }

            addHateosLinks(credencial);
            return credencial;
        });

        Link findAllLink = WebMvcLinkBuilder.linkTo(
                methodOn(CredencialController.class).findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                )
        ).withSelfRel();
        return assembler.toModel(credenciaisWithLinks, findAllLink);
    }

    public CredencialDTO findById(long id) {
        logger.info(String.format("Procurando um credencial pelo Id"));

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado"));

        var dto = parseObject(entity, CredencialDTO.class);

        // Preenchendo os campos do funcionário
        if(entity.getFuncionario() != null){
            dto.setFuncionarioId(entity.getFuncionario().getId());
            dto.setFuncionarioNome(entity.getFuncionario().getNome());
            dto.setFuncionarioMatricula(entity.getFuncionario().getMatricula());
        }

        addHateosLinks(dto);
        return dto;
    }


    public Credencial create(Credencial credencial) {
        logger.info("Criando uma nova credencial no banco");

        if (credencial == null) throw new RequiredObjectIsNullException();

        // Busca o funcionário pelo ID informado no DTO
        Funcionario funcionario = funcionarioRepository.findById(credencial.getFuncionario().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario não encontrado"));

        // Cria a entidade
        Credencial entity = new Credencial();
        entity.setTipo(credencial.getTipo());
        entity.setFuncionario(funcionario);
        entity.setDataEmissao(credencial.getDataEmissao());
        entity.setDataVencimento(credencial.getDataVencimento());
        entity.setStatus(credencial.getStatus());

        // Salva no banco
        Credencial savedEntity = repository.save(entity);

        // Converte para DTO e adiciona HATEOAS links
        var dto = parseObject(savedEntity, CredencialDTO.class);
        addHateosLinks(dto);

        return repository.save(entity);
    }



    public CredencialDTO update(CredencialDTO credencial) {
        logger.info("Atualizando uma credencial no banco");

        if (credencial == null) throw new RequiredObjectIsNullException();

        // Busca a credencial existente
        Credencial entity = repository.findById(credencial.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Id não encontrado no banco"));

        // Atualiza os campos simples
        entity.setTipo(credencial.getTipo());
        entity.setDataEmissao(credencial.getDataEmissao());
        entity.setDataVencimento(credencial.getDataVencimento());
        entity.setStatus(credencial.getStatus());

        // Atualiza o funcionário associado
        if (credencial.getFuncionarioId() != null) {
            Funcionario funcionario = funcionarioRepository.findById(credencial.getFuncionarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Funcionario não encontrado"));
            entity.setFuncionario(funcionario);
        }

        // Salva e retorna o DTO
        var dto = parseObject(repository.save(entity), CredencialDTO.class);
        addHateosLinks(dto);
        return dto;
    }
    public PagedModel<EntityModel<CredencialDTO>> findByStatus(
            CredencialStatus status, Pageable pageable) {

        logger.info("Procurando funcionários pela situação");

        // Busca a página de entidades
        Page<Credencial> CredencialPage = repository.findByStatus(status, pageable);

        // Converte para DTO e adiciona links
        Page<CredencialDTO> CredencialDTOPage = CredencialPage.map(funcionario -> {
            CredencialDTO dto = parseObject(funcionario, CredencialDTO.class);
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
        return assembler.toModel(CredencialDTOPage, findAllLink);
    }
    public PagedModel<EntityModel<CredencialDTO>> findCredencialEmited(
            LocalDate startDate, LocalDate endDate, Pageable pageable) {

        logger.info("Procurando credenciais por data de emissão");

        // Busca a página de entidades
        Page<Credencial> credencialPage = repository.findCredencialEmited(startDate, endDate, pageable);

        // Converte para DTO e adiciona links
        Page<CredencialDTO> credencialDTOPage = credencialPage.map(credencial -> {
            CredencialDTO dto = parseObject(credencial, CredencialDTO.class);
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
        return assembler.toModel(credencialDTOPage, findAllLink);
    }

    public PagedModel<EntityModel<CredencialDTO>> findCredencialExpiring(
            LocalDate startDate, LocalDate endDate, Pageable pageable) {

        logger.info("Procurando credenciais por data de vencimento");

        // Busca a página de entidades
        Page<Credencial> credencialPage = repository.findCredencialExpiring(startDate, endDate, pageable);

        // Converte para DTO e adiciona links
        Page<CredencialDTO> credencialDTOPage = credencialPage.map(credencial -> {
            CredencialDTO dto = parseObject(credencial, CredencialDTO.class);
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
        return assembler.toModel(credencialDTOPage, findAllLink);
    }


    public void delete(long id) {
        logger.info(String.format("Apagando um credencial do banco"));

        Credencial entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id nao encontrado no banco"));
        repository.delete(entity);
    }

    private static void addHateosLinks(CredencialDTO dto) {
        dto.add(linkTo(methodOn(CredencialController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(CredencialController.class).delete(dto.getId())).withRel("delete").withType("GET"));
        dto.add(linkTo(methodOn(CredencialController.class).create(parseObject(dto, Credencial.class))).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(CredencialController.class).update(dto)).withRel("update").withType("PUT"));
        //dto.add(linkTo(methodOn(CredencialController.class).findByName("",1, 12, "asc")).withRel("findByName").withType("GET"));
    }

}

