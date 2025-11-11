package com.example.projetoRestSpringBoot.file.importer.impl;

import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.file.importer.contract.FileImporter;
import com.example.projetoRestSpringBoot.model.Funcionario;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class XlsxImporter implements FileImporter {
    @Override
    public List<Funcionario> importarFuncionarios (InputStream inputStream) throws Exception {
        try(XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if(rowIterator.hasNext()){
                rowIterator.next();
            }
            
            return parseRowsToFuncionario(rowIterator);
        }
    }

    private List<Funcionario> parseRowsToFuncionario(Iterator<Row> rowIterator) {
        List<Funcionario> ListaFuncionarios = new ArrayList<>();
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
            if(isRowValid(row)){
                ListaFuncionarios.add(parseRowsToFuncionario(row));
            }
        }
        return ListaFuncionarios;
    }

    private Funcionario parseRowsToFuncionario(Row row) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(getCellStringValue(row, 0));
        funcionario.setCpf(getCellStringValue(row, 1));
        funcionario.setRg(getCellStringValue(row, 2));
        funcionario.setDataNascimento(getCellDateValue(row, 3));
        funcionario.setMatricula(getCellStringValue(row, 4));
        funcionario.setCargo(getCellStringValue(row, 5));
        funcionario.setDepartamento(getCellStringValue(row, 6));
        funcionario.setDataAdmissao(getCellDateValue(row, 7));
        funcionario.setSituacao(Enum.valueOf(com.example.projetoRestSpringBoot.enums.FuncionarioSituacao.class, getCellStringValue(row, 8)));
        funcionario.setTipoContrato(Enum.valueOf(com.example.projetoRestSpringBoot.enums.FuncionarioTipoContrato.class, getCellStringValue(row, 9)));
        funcionario.setEmail(getCellStringValue(row, 10));
        funcionario.setTelefone(getCellStringValue(row, 11));

        return funcionario;
    }

    private String getCellStringValue(Row row, int columnIndex) {
        var cell = row.getCell(columnIndex);
        if (cell == null) return "";

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return String.valueOf((long) cell.getNumericCellValue());
            }
            return cell.getStringCellValue().trim();
        } catch (Exception e) {
            return "";
        }
    }


    private LocalDate getCellDateValue(Row row, int columnIndex) {
        var cell = row.getCell(columnIndex);
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.STRING) {
                String dateString = cell.getStringCellValue().trim();
                if (dateString.isEmpty()) return null;
                return LocalDate.parse(dateString);
            }
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static boolean isRowValid(Row row) {
        return row.getCell(0) != null && row.getCell(0).getCellType() != CellType.BLANK;
    }

    @Override
    public List<CursoDTO> importarCursos(InputStream inputStream) throws Exception {
        try(XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if(rowIterator.hasNext()){
                rowIterator.next();
            }

            return parseRowsToCursoDTOList(rowIterator);
        }
}

    private List<CursoDTO> parseRowsToCursoDTOList(Iterator<Row> rowIterator) {
        List<CursoDTO> listaCursos = new ArrayList<>();
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
            if(isRowValid(row)){
                listaCursos.add(parseRowsToCursoDTOs(row));
            }
        }
        return listaCursos;
    }

    private CursoDTO parseRowsToCursoDTOs(Row row) {
        CursoDTO curso = new CursoDTO();
        curso.setNome(row.getCell(0).getStringCellValue());
        curso.setDescricao(row.getCell(1).getStringCellValue());
        curso.setCargaHoraria((int) row.getCell(2).getNumericCellValue());
        curso.setValidadeMeses((int) row.getCell(3).getNumericCellValue());
        curso.setOrigemCurso(Enum.valueOf(com.example.projetoRestSpringBoot.enums.CursoOrigem.class, row.getCell(4).getStringCellValue()));
        curso.setTipoObrigatoriedade(Enum.valueOf(com.example.projetoRestSpringBoot.enums.CursoTipoObrigatoriedade.class, row.getCell(5).getStringCellValue()));

        return curso;
    }

}
