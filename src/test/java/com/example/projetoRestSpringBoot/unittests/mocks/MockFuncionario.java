package com.example.projetoRestSpringBoot.unittests.mocks;


import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.enums.FuncionarioSituacao;
import com.example.projetoRestSpringBoot.enums.FuncionarioTipoContrato;
import com.example.projetoRestSpringBoot.model.Funcionario;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


    public class MockFuncionario {


        public Funcionario mockEntity() {
            return mockEntity(0);
        }

        public FuncionarioDTO mockDTO() {
            return mockDTO(0);
        }

        public List<Funcionario> mockEntityList() {
            List<Funcionario> funcionarios = new ArrayList<Funcionario>();
            for (int i = 0; i < 14; i++) {
                funcionarios.add(mockEntity(i));
            }
            return funcionarios;
        }

        public List<FuncionarioDTO> mockDTOList() {
            List<FuncionarioDTO> persons = new ArrayList<>();
            for (int i = 0; i < 14; i++) {
                persons.add(mockDTO(i));
            }
            return persons;
        }

        public Funcionario mockEntity(Integer number) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(number.longValue());
            funcionario.setNome("Name" + number);
            funcionario.setCpf("123.456.789-0" + number);
            funcionario.setRg("MG-12.345.67" + number);
            funcionario.setDataNascimento(LocalDate.of(1990,1,1).plusDays(number));
            funcionario.setMatricula(String.format("MAT%03d", number));
            funcionario.setCargo("Cargo Test" + number);
            funcionario.setDepartamento("Departamento Test" + number);
            funcionario.setDataAdmissao(LocalDate.of(2020,1,1).plusDays(number));
            funcionario.setSituacao(FuncionarioSituacao.ATIVO);
            funcionario.setTipoContrato(FuncionarioTipoContrato.CLT);
            funcionario.setEmail("email" + number);
            funcionario.setTelefone("Telefone Test" + number);
            return funcionario;
        }

        public FuncionarioDTO mockDTO(Integer number) {
            FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
            funcionarioDTO.setId(number.longValue());
            funcionarioDTO.setNome("Name" + number);
            funcionarioDTO.setMatricula("MAT" + number);
            funcionarioDTO.setCargo("Cargo Test" + number);
            funcionarioDTO.setDepartamento("Departamento Test" + number);
            funcionarioDTO.setDataAdmissao(LocalDate.of(2020,1,1).plusDays(number));
            funcionarioDTO.setSituacao(FuncionarioSituacao.ATIVO);
            funcionarioDTO.setTipoContrato(FuncionarioTipoContrato.CLT);
            funcionarioDTO.setEmail("email" + number);
            funcionarioDTO.setTelefone("Telefone Test" + number);
            return funcionarioDTO;
        }

    }
