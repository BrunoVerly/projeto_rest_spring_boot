package com.example.projetoRestSpringBoot.unittests.mocks;

import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.enums.CredencialStatus;
import com.example.projetoRestSpringBoot.enums.CredencialTipo;
import com.example.projetoRestSpringBoot.model.Credencial;
import com.example.projetoRestSpringBoot.model.Funcionario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MockCredencial {

    public Credencial mockEntity() {
        return mockEntity(0);
    }

    public CredencialDTO mockDTO() {
        return mockDTO(0);
    }

    public List<Credencial> mockEntityList() {
        List<Credencial> credenciais = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            credenciais.add(mockEntity(i));
        }
        return credenciais;
    }

    public List<CredencialDTO> mockDTOList() {
        List<CredencialDTO> credenciais = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            credenciais.add(mockDTO(i));
        }
        return credenciais;
    }

    public Credencial mockEntity(Integer number) {
        Credencial credencial = new Credencial();
        credencial.setId(number.longValue());
        credencial.setTipo(number % 2 == 0 ? CredencialTipo.PERMANENTE : CredencialTipo.TEMPORARIA);
        credencial.setDataEmissao(LocalDate.now().minusMonths(number));
        credencial.setDataVencimento(LocalDate.now().plusMonths(12 - number));
        credencial.setStatus(number % 2 == 0 ? CredencialStatus.VALIDA : CredencialStatus.VENCIDA);

        // Criar e associar um Funcionario mock
        Funcionario funcionario = new Funcionario();
        funcionario.setId((long) (1 + number));
        funcionario.setNome("Name" + number);
        funcionario.setMatricula("MAT" + number);
        credencial.setFuncionario(funcionario);

        return credencial;
    }

    public CredencialDTO mockDTO(Integer number) {
        CredencialDTO credencialDTO = new CredencialDTO();
        credencialDTO.setId(number.longValue());
        credencialDTO.setTipo(number % 2 == 0 ? CredencialTipo.PERMANENTE : CredencialTipo.TEMPORARIA);
        credencialDTO.setFuncionarioId((long) (1 + number));
        credencialDTO.setFuncionarioNome("Funcionário Test " + number);
        credencialDTO.setFuncionarioMatricula("MAT" + String.format("%04d", number));
        credencialDTO.setDataEmissao(LocalDate.now().minusMonths(number));
        credencialDTO.setDataVencimento(LocalDate.now().plusMonths(12 - number));
        credencialDTO.setStatus(number % 2 == 0 ? CredencialStatus.VALIDA : CredencialStatus.VENCIDA);
        return credencialDTO;
    }
}
