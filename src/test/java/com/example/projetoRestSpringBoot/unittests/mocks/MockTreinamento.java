package com.example.projetoRestSpringBoot.unittests.mocks;

import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import com.example.projetoRestSpringBoot.enums.TreinamentoStatus;
import com.example.projetoRestSpringBoot.model.Treinamento;
import com.example.projetoRestSpringBoot.model.Funcionario;
import com.example.projetoRestSpringBoot.model.Curso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MockTreinamento {

    public Treinamento mockEntity() {
        return mockEntity(0);
    }

    public TreinamentoDTO mockDTO() {
        return mockDTO(0);
    }

    public List<Treinamento> mockEntityList() {
        List<Treinamento> treinamentos = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            treinamentos.add(mockEntity(i));
        }
        return treinamentos;
    }

    public List<TreinamentoDTO> mockDTOList() {
        List<TreinamentoDTO> treinamentos = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            treinamentos.add(mockDTO(i));
        }
        return treinamentos;
    }

    public Treinamento mockEntity(Integer number) {
        Treinamento treinamento = new Treinamento();
        treinamento.setId(number.longValue());
        treinamento.setDataAgendamento(LocalDate.now().plusDays(number));
        treinamento.setDataConcluido(LocalDate.now().plusDays(number + 5));
        treinamento.setDataVencimento(LocalDate.now().plusMonths(12 + number));
        treinamento.setInstrutor("Instrutor Test " + number);
        treinamento.setStatus(number % 2 == 0 ? TreinamentoStatus.VALIDO : TreinamentoStatus.VENCIDO);

        // Criar e associar um Funcionario mock
        Funcionario funcionario = new Funcionario();
        funcionario.setId((long) (1 + number));
        funcionario.setNome("Funcionário Test " + number);
        funcionario.setMatricula("MAT" + String.format("%04d", number));
        treinamento.setFuncionario(funcionario);

        // Criar e associar um Curso mock
        Curso curso = new Curso();
        curso.setId((long) (100 + number));
        curso.setNome("Curso Test " + number);
        treinamento.setCurso(curso);

        return treinamento;
    }

    public TreinamentoDTO mockDTO(Integer number) {
        TreinamentoDTO treinamentoDTO = new TreinamentoDTO();
        treinamentoDTO.setId(number.longValue());
        treinamentoDTO.setFuncionarioId((long) (1 + number));
        treinamentoDTO.setFuncionarioNome("Funcionário Test " + number);
        treinamentoDTO.setFuncionarioMatricula("MAT" + String.format("%04d", number));
        treinamentoDTO.setCursoId((long) (100 + number));
        treinamentoDTO.setCursoNome("Curso Test " + number);
        treinamentoDTO.setDataAgendamento(LocalDate.now().plusDays(number));
        treinamentoDTO.setDataConcluido(LocalDate.now().plusDays(number + 5));
        treinamentoDTO.setDataVencimento(LocalDate.now().plusMonths(12 + number));
        treinamentoDTO.setInstrutor("Instrutor Test " + number);
        treinamentoDTO.setStatus(number % 2 == 0 ? TreinamentoStatus.VALIDO : TreinamentoStatus.VENCIDO);
        return treinamentoDTO;
    }
}
