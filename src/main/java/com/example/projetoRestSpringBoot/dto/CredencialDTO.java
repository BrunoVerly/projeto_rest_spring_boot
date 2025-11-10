package com.example.projetoRestSpringBoot.dto;

import com.example.projetoRestSpringBoot.enums.CredencialStatus;
import com.example.projetoRestSpringBoot.enums.CredencialTipo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.time.LocalDate;


@Getter
@Setter
@Relation(collectionRelation = "curso")
public class CredencialDTO extends RepresentationModel<CredencialDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private CredencialTipo tipo;
    private Long funcionarioId;
    private String funcionarioNome;
    private String funcionarioMatricula;
    private LocalDate dataEmissao;
    private LocalDate dataVencimento;
    private CredencialStatus status;

    public CredencialDTO() {
    }
}
