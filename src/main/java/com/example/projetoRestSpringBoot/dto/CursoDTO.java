package com.example.projetoRestSpringBoot.dto;

import com.example.projetoRestSpringBoot.enums.CursoOrigem;
import com.example.projetoRestSpringBoot.enums.CursoTipoObrigatoriedade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.io.Serializable;


@Getter
@Setter
@Relation(collectionRelation = "curso")
public class CursoDTO extends RepresentationModel<CursoDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String descricao;
    private int cargaHoraria;
    private int validadeMeses;
    private CursoOrigem origemCurso;
    private CursoTipoObrigatoriedade tipoObrigatoriedade;

    public CursoDTO() {
    }
}
