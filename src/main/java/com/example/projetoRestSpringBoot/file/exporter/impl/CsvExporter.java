package com.example.projetoRestSpringBoot.file.exporter.impl;

import com.example.projetoRestSpringBoot.dto.*;
import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import com.example.projetoRestSpringBoot.model.Credencial;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.example.projetoRestSpringBoot.mapper.ObjectMapper.parseObject;

@Component
public class CsvExporter implements FileExporter {
    @Override
    public Resource exportarFuncionarios(List<FuncionarioDTO> funcionarios) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat.Builder.create().setHeader(
                "Id", "nome", "matricula", "cargo", "departametno" , "dataAdmissao", "situacao", "tipoContrato", "email", "telefone")
                .setSkipHeaderRecord(false)
                .build();

        try(CSVPrinter csvPrinter = new CSVPrinter(writter,csvFormat)) {
            for(FuncionarioDTO funcionario : funcionarios){
                csvPrinter.printRecord(
                        funcionario.getId(),
                        funcionario.getNome(),
                        funcionario.getMatricula(),
                        funcionario.getCargo(),
                        funcionario.getDepartamento(),
                        funcionario.getDataAdmissao(),
                        funcionario.getSituacao(),
                        funcionario.getTipoContrato(),
                        funcionario.getEmail(),
                        funcionario.getTelefone()
                );

            }
        }
        return new ByteArrayResource(outputStream.toByteArray());
    }

    @Override
    public Resource exportarCredenciais(List<CredencialDTO> credenciais) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setHeader(
                        "Id",
                        "Tipo",
                        "Funcionario_Id",
                        "Funcionario_Nome",
                        "Funcionario_Matricula",
                        "Data_Emissao",
                        "Data_Vencimento",
                        "Status"
                )
                .setSkipHeaderRecord(false)
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (CredencialDTO credencial : credenciais) {
                csvPrinter.printRecord(
                        credencial.getId(),
                        credencial.getTipo(),
                        credencial.getFuncionarioId(),
                        credencial.getFuncionarioNome(),
                        credencial.getFuncionarioMatricula(),
                        credencial.getDataEmissao(),
                        credencial.getDataVencimento(),
                        credencial.getStatus()
                );
            }
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }


    @Override
    public Resource exportTreinamentos(List<TreinamentoDTO> treinamentos) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat.Builder.create().setHeader(
                        "Id", "Funcionario_Id", "Funcionario_Nome", "Funcionario_Matricula", "Curso_Id" , "Curso_Nome", "Data_Agendamento", "Data_Concluido", "Data_Vencimento", "Instrutor", "Status")
                .setSkipHeaderRecord(false)
                .build();

        try(CSVPrinter csvPrinter = new CSVPrinter(writter,csvFormat)) {
            for(TreinamentoDTO treinamento : treinamentos){
                csvPrinter.printRecord(
                        treinamento.getId(),
                        treinamento.getFuncionarioId() != null ? treinamento.getFuncionarioId() : "",
                        treinamento.getFuncionarioNome() != null ? treinamento.getFuncionarioNome() : "",
                        treinamento.getFuncionarioMatricula() != null ? treinamento.getFuncionarioMatricula() : "",
                        treinamento.getCursoId() != null ? treinamento.getCursoId() : "",
                        treinamento.getCursoNome() != null ? treinamento.getCursoNome() : "",
                        treinamento.getDataAgendamento() != null ? treinamento.getDataAgendamento() : "",
                        treinamento.getDataConcluido() != null ? treinamento.getDataConcluido() : "",
                        treinamento.getDataVencimento() != null ? treinamento.getDataVencimento() : "",
                        treinamento.getInstrutor() != null ? treinamento.getInstrutor() : "",
                        treinamento.getStatus() != null ? treinamento.getStatus() : ""
                );
            }
        }
        return new ByteArrayResource(outputStream.toByteArray());
    }

    @Override
    public Resource exportCursos(List<CursoDTO> cursos) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat.Builder.create().setHeader(
                        "Id", "Nome", "Descricao", "Carga_Horaria" , "Validade_Meses", "Origem_Curso", "Tipo_Obrigatoriedade")
                .setSkipHeaderRecord(false)
                .build();

        try(CSVPrinter csvPrinter = new CSVPrinter(writter,csvFormat)) {
            for(CursoDTO curso : cursos){
                csvPrinter.printRecord(
                        curso.getId(),
                        curso.getNome(),
                        curso.getDescricao(),
                        curso.getCargaHoraria(),
                        curso.getValidadeMeses(),
                        curso.getOrigemCurso(),
                        curso.getTipoObrigatoriedade()
                );

            }
        }
        return new ByteArrayResource(outputStream.toByteArray());
    }
}

