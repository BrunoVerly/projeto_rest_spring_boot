package com.example.projetoRestSpringBoot.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;
import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseListObjects;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.projetoRestSpringBoot.unittests.mocks.*;
import com.example.projetoRestSpringBoot.dto.*;
import com.example.projetoRestSpringBoot.model.*;

public class ObjectMapperTests {

    private MockFuncionario mockFuncionario;
    private MockCurso mockCurso;
    private MockTreinamento mockTreinamento;
    private MockCredencial mockCredencial;

    @BeforeEach
    public void setUp() {
        mockFuncionario = new MockFuncionario();
        mockCurso = new MockCurso();
        mockTreinamento = new MockTreinamento();
        mockCredencial = new MockCredencial();
    }

    // FUNCIONARIO TESTS
    @Test
    public void parseFuncionarioEntityToDTOTest() {
        FuncionarioDTO output = parseObject(mockFuncionario.mockEntity(1), FuncionarioDTO.class);
        assertEquals(1L, output.getId());
        assertNotNull(output.getNome());
        assertNotNull(output.getMatricula());
        assertNotNull(output.getCargo());
        assertNotNull(output.getDepartamento());
    }

    @Test
    public void parseFuncionarioEntityListToDTOListTest() {
        List<FuncionarioDTO> outputList = parseListObjects(mockFuncionario.mockEntityList(), FuncionarioDTO.class);
        assertEquals(14, outputList.size());

        FuncionarioDTO outputZero = outputList.get(0);
        assertEquals(0L, outputZero.getId());

        FuncionarioDTO outputSeven = outputList.get(7);
        assertEquals(7L, outputSeven.getId());
    }

    @Test
    public void parseFuncionarioDTOToEntityTest() {
        Funcionario output = parseObject(mockFuncionario.mockDTO(1), Funcionario.class);
        assertEquals(1L, output.getId());
        assertNotNull(output.getNome());
        assertNotNull(output.getMatricula());
    }

    @Test
    public void parseFuncionarioDTOListToEntityListTest() {
        List<Funcionario> outputList = parseListObjects(mockFuncionario.mockDTOList(), Funcionario.class);
        assertEquals(14, outputList.size());

        Funcionario outputZero = outputList.get(0);
        assertEquals(0L, outputZero.getId());

        Funcionario outputSeven = outputList.get(7);
        assertEquals(7L, outputSeven.getId());
    }

    // CURSO TESTS
    @Test
    public void parseCursoEntityToDTOTest() {
        CursoDTO output = parseObject(mockCurso.mockEntity(1), CursoDTO.class);
        assertEquals(1L, output.getId());
        assertNotNull(output.getNome());
        assertNotNull(output.getDescricao());
    }

    @Test
    public void parseCursoEntityListToDTOListTest() {
        List<CursoDTO> outputList = parseListObjects(mockCurso.mockEntityList(), CursoDTO.class);
        assertEquals(14, outputList.size());

        CursoDTO outputZero = outputList.get(0);
        assertEquals(0L, outputZero.getId());

        CursoDTO outputSeven = outputList.get(7);
        assertEquals(7L, outputSeven.getId());
    }

    @Test
    public void parseCursoDTOToEntityTest() {
        Curso output = parseObject(mockCurso.mockDTO(1), Curso.class);
        assertEquals(1L, output.getId());
        assertNotNull(output.getNome());
    }

    @Test
    public void parseCursoDTOListToEntityListTest() {
        List<Curso> outputList = parseListObjects(mockCurso.mockDTOList(), Curso.class);
        assertEquals(14, outputList.size());

        Curso outputZero = outputList.get(0);
        assertEquals(0L, outputZero.getId());

        Curso outputSeven = outputList.get(7);
        assertEquals(7L, outputSeven.getId());
    }

    // CREDENCIAL TESTS
    @Test
    public void parseCredencialEntityToDTOTest() {
        CredencialDTO output = parseObject(mockCredencial.mockEntity(1), CredencialDTO.class);
        assertEquals(1L, output.getId());
        assertNotNull(output.getTipo());
        assertNotNull(output.getDataEmissao());
        assertNotNull(output.getDataVencimento());
        assertNotNull(output.getStatus());
    }

    @Test
    public void parseCredencialEntityListToDTOListTest() {
        List<CredencialDTO> outputList = parseListObjects(mockCredencial.mockEntityList(), CredencialDTO.class);
        assertEquals(14, outputList.size());

        CredencialDTO outputZero = outputList.get(0);
        assertEquals(0L, outputZero.getId());

        CredencialDTO outputSeven = outputList.get(7);
        assertEquals(7L, outputSeven.getId());
    }

    @Test
    public void parseCredencialDTOToEntityTest() {
        Credencial output = parseObject(mockCredencial.mockDTO(1), Credencial.class);
        assertEquals(1L, output.getId());
        assertNotNull(output.getTipo());
    }

    @Test
    public void parseCredencialDTOListToEntityListTest() {
        List<Credencial> outputList = parseListObjects(mockCredencial.mockDTOList(), Credencial.class);
        assertEquals(14, outputList.size());

        Credencial outputZero = outputList.get(0);
        assertEquals(0L, outputZero.getId());

        Credencial outputSeven = outputList.get(7);
        assertEquals(7L, outputSeven.getId());
    }

    // TREINAMENTO TESTS
    @Test
    public void parseTreinamentoEntityToDTOTest() {
        TreinamentoDTO output = parseObject(mockTreinamento.mockEntity(1), TreinamentoDTO.class);
        assertEquals(1L, output.getId());
        assertNotNull(output.getDataAgendamento());
        assertNotNull(output.getStatus());
    }

    @Test
    public void parseTreinamentoEntityListToDTOListTest() {
        List<TreinamentoDTO> outputList = parseListObjects(mockTreinamento.mockEntityList(), TreinamentoDTO.class);
        assertEquals(14, outputList.size());

        TreinamentoDTO outputZero = outputList.get(0);
        assertEquals(0L, outputZero.getId());

        TreinamentoDTO outputSeven = outputList.get(7);
        assertEquals(7L, outputSeven.getId());
    }

    @Test
    public void parseTreinamentoDTOToEntityTest() {
        Treinamento output = parseObject(mockTreinamento.mockDTO(1), Treinamento.class);
        assertEquals(1L, output.getId());
    }

    @Test
    public void parseTreinamentoDTOListToEntityListTest() {
        List<Treinamento> outputList = parseListObjects(mockTreinamento.mockDTOList(), Treinamento.class);
        assertEquals(14, outputList.size());

        Treinamento outputZero = outputList.get(0);
        assertEquals(0L, outputZero.getId());

        Treinamento outputSeven = outputList.get(7);
        assertEquals(7L, outputSeven.getId());
    }
}
