package com.example.projetoRestSpringBoot.dto;

import com.example.projetoRestSpringBoot.enums.TreinamentoStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.io.Serializable;
import java.time.LocalDate;


@Getter
@Setter
@Relation(collectionRelation = "treinamento")
public class TreinamentoDTO extends RepresentationModel<TreinamentoDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long funcionarioId;
    private String funcionarioNome;
    private String funcionarioMatricula;
    private Long cursoId;
    private String cursoNome;
    private LocalDate dataAgendamento;
    private LocalDate dataConcluido;
    private LocalDate dataVencimento;
    private String instrutor;
    private TreinamentoStatus status;

    public TreinamentoDTO() {
    }
}
