package com.example.projetoRestSpringBoot.model;

import com.example.projetoRestSpringBoot.enums.CursoOrigem;
import com.example.projetoRestSpringBoot.enums.CursoTipoObrigatoriedade;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "curso")
@Getter
@Setter
public class Curso implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name= "carga_horaria", nullable = false)
    private int cargaHoraria;

    @Column(name="validade_meses", nullable = false)
    private int validadeMeses;

    @Enumerated(EnumType.STRING)
    @Column(name="origem_curso", nullable = false, length = 7)
    private CursoOrigem origemCurso;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_obrigatoriedade", nullable = false)
    private CursoTipoObrigatoriedade tipoObrigatoriedade;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Curso curso = (Curso) o;
        return Objects.equals(id, curso.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
