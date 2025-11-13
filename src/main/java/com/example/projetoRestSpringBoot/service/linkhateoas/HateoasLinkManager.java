package com.example.projetoRestSpringBoot.service.linkhateoas;

import com.example.projetoRestSpringBoot.controller.CredencialController;
import com.example.projetoRestSpringBoot.controller.CursoController;
import com.example.projetoRestSpringBoot.controller.FuncionarioController;
import com.example.projetoRestSpringBoot.controller.TreinamentoController;
import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public final class HateoasLinkManager {

    private HateoasLinkManager() {
    }

    // ============ CURSO LINKS ============
    public static void addCursoDetailLinks(CursoDTO dto) {
        dto.add(linkTo(methodOn(CursoController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(CursoController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(CursoController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(CursoController.class).findAll(0, 12, "asc")).withRel("findAll").withType("GET"));
    }

    public static void addCursoListPageLinks(PagedModel<EntityModel<CursoDTO>> model) {
        model.add(linkTo(methodOn(CursoController.class).create(null)).withRel("create").withType("POST"));
        model.add(linkTo(methodOn(CursoController.class).importarCursos(null)).withRel("importar").withType("POST"));
        model.add(linkTo(methodOn(CursoController.class).exportPage(0, 12, "asc", null)).withRel("exportPage").withType("GET"));
    }

    // ============ FUNCIONÁRIO LINKS ============
    public static void addFuncionarioDetailLinks(FuncionarioDTO dto) {
        dto.add(linkTo(methodOn(FuncionarioController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(FuncionarioController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(FuncionarioController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(FuncionarioController.class).findBySituacao(dto.getSituacao(), 0, 12, "asc")).withRel("situacao").withType("GET"));
        dto.add(linkTo(methodOn(FuncionarioController.class).findAll(0, 12, "asc")).withRel("findAll").withType("GET"));
    }

    public static void addFuncionarioListPageLinks(PagedModel<EntityModel<FuncionarioDTO>> model) {
        model.add(linkTo(methodOn(FuncionarioController.class).create(null)).withRel("create").withType("POST"));
        model.add(linkTo(methodOn(FuncionarioController.class).importarFuncionarios(null)).withRel("importar").withType("POST"));
        model.add(linkTo(methodOn(FuncionarioController.class).exportPage(0, 12, "asc", null)).withRel("exportPage").withType("GET"));
    }

    // ============ TREINAMENTO LINKS ============
    public static void addTreinamentoDetailLinks(TreinamentoDTO dto) {
        dto.add(linkTo(methodOn(TreinamentoController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(TreinamentoController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(TreinamentoController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(TreinamentoController.class).findAll(0, 12, "asc")).withRel("findAll").withType("GET"));
    }

    public static void addTreinamentoListPageLinks(PagedModel<EntityModel<TreinamentoDTO>> model) {
        model.add(linkTo(methodOn(TreinamentoController.class).create(null)).withRel("create").withType("POST"));
        model.add(linkTo(methodOn(TreinamentoController.class).exportPage(0, 12, "asc", null)).withRel("exportPage").withType("GET"));
    }

    // ============ CREDENCIAL LINKS ============
    public static void addCredencialDetailLinks(CredencialDTO dto) {
        dto.add(linkTo(methodOn(CredencialController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(CredencialController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(CredencialController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(CredencialController.class).findByStatus(dto.getStatus(), 0, 12, "asc")).withRel("status").withType("GET"));
        dto.add(linkTo(methodOn(CredencialController.class).findAll(0, 12, "asc")).withRel("findAll").withType("GET"));
    }

    public static void addCredencialListPageLinks(PagedModel<EntityModel<CredencialDTO>> model) {
        model.add(linkTo(methodOn(CredencialController.class).create(null)).withRel("create").withType("POST"));
        model.add(linkTo(methodOn(CredencialController.class).exportPage(0, 12, "asc", null)).withRel("exportPage").withType("GET"));
    }

}
