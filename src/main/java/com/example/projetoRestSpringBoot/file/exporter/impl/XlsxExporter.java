package com.example.projetoRestSpringBoot.file.exporter.impl;

import com.example.projetoRestSpringBoot.dto.CredencialDTO;
import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.dto.TreinamentoDTO;
import com.example.projetoRestSpringBoot.file.exporter.contract.FileExporter;
import com.example.projetoRestSpringBoot.model.Curso;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class XlsxExporter implements FileExporter {
    @Override
    public Resource exportarFuncionarios(List<FuncionarioDTO> funcionarios) throws Exception {
        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("funcionarios");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Id", "nome", "matricula", "cargo", "departamento", "dataAdmissao", "situacao", "tipoContrato", "email", "telefone"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }
            int rowIdx = 1;
            for (FuncionarioDTO funcionario : funcionarios) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(funcionario.getId());
                row.createCell(1).setCellValue(funcionario.getNome());
                row.createCell(2).setCellValue(funcionario.getMatricula());
                row.createCell(3).setCellValue(funcionario.getCargo());
                row.createCell(4).setCellValue(funcionario.getDepartamento());
                row.createCell(5).setCellValue(funcionario.getDataAdmissao().toString());
                row.createCell(6).setCellValue(funcionario.getSituacao().toString());
                row.createCell(7).setCellValue(funcionario.getTipoContrato().toString());
                row.createCell(8).setCellValue(funcionario.getEmail());
                row.createCell(9).setCellValue(funcionario.getTelefone());
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }

    @Override
    public Resource exportarCredenciais(List<CredencialDTO> credenciais) throws Exception {
        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("credenciais");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"id", "tipo", "funcionarioId", "funcionarioNome", "funcionarioMatricula", "dataEmissao", "dataVencimento", "status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }
            int rowIdx = 1;
            for (CredencialDTO credencial : credenciais) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(credencial.getId());
                row.createCell(1).setCellValue(credencial.getTipo().toString());
                row.createCell(2).setCellValue(credencial.getFuncionarioId());
                row.createCell(3).setCellValue(credencial.getFuncionarioNome());
                row.createCell(4).setCellValue(credencial.getFuncionarioMatricula());
                row.createCell(5).setCellValue(credencial.getDataEmissao().toString());
                row.createCell(6).setCellValue(credencial.getDataVencimento().toString());
                row.createCell(7).setCellValue(credencial.getStatus().toString());

            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }



    @Override
    public Resource exportTreinamentos(List<TreinamentoDTO> treinamentos) throws Exception {
        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("treinamentos");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"id", "funcionarioId", "funcionarioNome", "funcionarioMatricula", "cursoId", "cursoNome", "dataAgendamento", "dataConcluido", "dataVencimento", "instrutor", "status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }
            int rowIdx = 1;
            for (TreinamentoDTO treinamento : treinamentos) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(treinamento.getId());
                row.createCell(1).setCellValue(treinamento.getFuncionarioId());
                row.createCell(2).setCellValue(treinamento.getFuncionarioNome());
                row.createCell(3).setCellValue(treinamento.getFuncionarioMatricula());
                row.createCell(4).setCellValue(treinamento.getCursoId());
                row.createCell(5).setCellValue(treinamento.getCursoNome());
                row.createCell(6).setCellValue(treinamento.getDataAgendamento().toString());
                row.createCell(7).setCellValue(treinamento.getDataConcluido() != null ? treinamento.getDataConcluido().toString() : "");
                row.createCell(8).setCellValue(treinamento.getDataVencimento().toString());
                row.createCell(9).setCellValue(treinamento.getInstrutor());
                row.createCell(10).setCellValue(treinamento.getStatus().toString());
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }

    @Override
    public Resource exportCursos(List<CursoDTO> cursos) throws Exception {
        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("cursos");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"id","nome","descricao","cargaHoraria","validadeMeses","origemCurso","tipoObrigatoriedade"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }
            int rowIdx = 1;
            for (CursoDTO curso : cursos) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(curso.getId());
                row.createCell(1).setCellValue(curso.getNome());
                row.createCell(2).setCellValue(curso.getDescricao());
                row.createCell(3).setCellValue(curso.getCargaHoraria());
                row.createCell(4).setCellValue(curso.getValidadeMeses());
                row.createCell(5).setCellValue(curso.getOrigemCurso().toString());
                row.createCell(6).setCellValue(curso.getTipoObrigatoriedade().toString());
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}