package com.example.projetoRestSpringBoot.file.importer.impl;

import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.enums.CursoOrigem;
import com.example.projetoRestSpringBoot.enums.CursoTipoObrigatoriedade;
import com.example.projetoRestSpringBoot.enums.FuncionarioSituacao;
import com.example.projetoRestSpringBoot.enums.FuncionarioTipoContrato;
import com.example.projetoRestSpringBoot.file.importer.contract.FileImporter;
import com.example.projetoRestSpringBoot.model.Funcionario;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvImporter implements FileImporter {
    @Override
    public List<FuncionarioDTO> importarFuncionarios(InputStream inputStream) throws Exception {
        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(new InputStreamReader(inputStream));

        return parseRecordsToPersonDTOs(records);
    }

    @Override
    public List<CursoDTO> importarCursos(InputStream inputStream) throws Exception {
        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(new InputStreamReader(inputStream));

        return parseRecordsToCursoDTOs(records);
    }

    private List<FuncionarioDTO> parseRecordsToPersonDTOs(Iterable<CSVRecord> records) {
        List<FuncionarioDTO> listaFuncionarios = new ArrayList<>();
        for (CSVRecord record : records) {
            FuncionarioDTO funcionario = new FuncionarioDTO();
            funcionario.setNome(record.get("nome"));
            funcionario.setMatricula(record.get("matricula"));
            funcionario.setCargo(record.get("cargo"));
            funcionario.setDepartamento(record.get("departamento"));
            funcionario.setDataAdmissao(LocalDate.parse(record.get("dataAdmissao")));
            funcionario.setSituacao(FuncionarioSituacao.valueOf(record.get("situacao")));
            funcionario.setTipoContrato(FuncionarioTipoContrato.valueOf(record.get("tipoContrato")));
            funcionario.setEmail(record.get("email"));
            funcionario.setTelefone(record.get("telefone"));
            listaFuncionarios.add(funcionario);
        }
        return listaFuncionarios;
    }

    private List<CursoDTO> parseRecordsToCursoDTOs(Iterable<CSVRecord> records) {
        List<CursoDTO> listaCursos = new ArrayList<>();
        for (CSVRecord record : records) {
            CursoDTO curso = new CursoDTO();
            curso.setNome(record.get("nome"));
            curso.setDescricao(record.get("descricao"));
            curso.setCargaHoraria(Integer.parseInt(record.get("cargaHoraria")));
            curso.setValidadeMeses(Integer.parseInt(record.get("validadeMeses")));
            curso.setOrigemCurso(CursoOrigem.valueOf(record.get("origemCurso")));
            curso.setTipoObrigatoriedade(CursoTipoObrigatoriedade.valueOf(record.get("tipoObrigatoriedade")));
            listaCursos.add(curso);
        }
        return listaCursos;
    }
}