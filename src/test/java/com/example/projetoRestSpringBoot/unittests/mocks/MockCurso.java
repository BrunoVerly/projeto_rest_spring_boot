package com.example.projetoRestSpringBoot.unittests.mocks;

import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.enums.CursoOrigem;
import com.example.projetoRestSpringBoot.enums.CursoTipoObrigatoriedade;
import com.example.projetoRestSpringBoot.model.Curso;

import java.util.ArrayList;
import java.util.List;

public class MockCurso {

    public Curso mockEntity() {
        return mockEntity(0);
    }

    public CursoDTO mockDTO() {
        return mockDTO(0);
    }

    public List<Curso> mockEntityList() {
        List<Curso> cursos = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            cursos.add(mockEntity(i));
        }
        return cursos;
    }

    public List<CursoDTO> mockDTOList() {
        List<CursoDTO> cursos = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            cursos.add(mockDTO(i));
        }
        return cursos;
    }

    public Curso mockEntity(Integer number) {
        Curso curso = new Curso();
        curso.setId(number.longValue());
        curso.setNome("Curso Test " + number);
        curso.setDescricao("Descrição do curso " + number + " com conteúdo detalhado.");
        curso.setCargaHoraria(20 + number);
        curso.setValidadeMeses(12 + number);
        curso.setOrigemCurso(number % 2 == 0 ? CursoOrigem.INTERNO : CursoOrigem.EXTERNO);
        curso.setTipoObrigatoriedade(number % 2 == 0 ? CursoTipoObrigatoriedade.OBRIGATORIO : CursoTipoObrigatoriedade.ADICIONAL);
        return curso;
    }

    public CursoDTO mockDTO(Integer number) {
        CursoDTO cursoDTO = new CursoDTO();
        cursoDTO.setId(number.longValue());
        cursoDTO.setNome("Curso Test " + number);
        cursoDTO.setDescricao("Descrição do curso " + number + " com conteúdo detalhado.");
        cursoDTO.setCargaHoraria(20 + number);
        cursoDTO.setValidadeMeses(12 + number);
        cursoDTO.setOrigemCurso(number % 2 == 0 ? CursoOrigem.INTERNO : CursoOrigem.EXTERNO);
        cursoDTO.setTipoObrigatoriedade(number % 2 == 0 ? CursoTipoObrigatoriedade.OBRIGATORIO : CursoTipoObrigatoriedade.ADICIONAL);
        return cursoDTO;
    }
}
