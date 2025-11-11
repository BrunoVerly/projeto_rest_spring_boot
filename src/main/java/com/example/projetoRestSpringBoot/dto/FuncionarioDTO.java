package com.example.projetoRestSpringBoot.dto;

import com.example.projetoRestSpringBoot.enums.FuncionarioSituacao;
import com.example.projetoRestSpringBoot.enums.FuncionarioTipoContrato;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

@Relation(collectionRelation = "funcionarios")
@Getter
@Setter
public class FuncionarioDTO extends RepresentationModel<FuncionarioDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String matricula;
    private String cargo;
    private String departamento;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataAdmissao;
    private FuncionarioSituacao situacao;
    private FuncionarioTipoContrato tipoContrato;
    private String email;
    private String telefone;


    public FuncionarioDTO() {
    }

}
