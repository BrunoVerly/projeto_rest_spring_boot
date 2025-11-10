package com.example.projetoRestSpringBoot.model;

import com.example.projetoRestSpringBoot.enums.TreinamentoStatus;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "treinamento")
@Getter
@Setter
public class Treinamento implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name= "data_agendamento", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataAgendamento;

    @Column(name= "data_concluido")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataConcluido;

    @Column(name= "data_vencimento", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVencimento;

    @Column(name="instrutor")
    private String instrutor;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private TreinamentoStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Treinamento that = (Treinamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
