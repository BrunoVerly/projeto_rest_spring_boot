package com.example.projetoRestSpringBoot.file.importer.impl;

import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;
import com.example.projetoRestSpringBoot.file.importer.contract.FileImporter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class XlsxImporter implements FileImporter {
    @Override
    public List<FuncionarioDTO> importarFuncionarios (InputStream inputStream) throws Exception {
        try(XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if(rowIterator.hasNext()){
                rowIterator.next();
            }
            
            return parseRowsToFuncionarioDTOList(rowIterator);
        }
    }

    private List<FuncionarioDTO> parseRowsToFuncionarioDTOList(Iterator<Row> rowIterator) {
        List<FuncionarioDTO> ListaFuncionarios = new ArrayList<>();
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
            if(isRowValid(row)){
                ListaFuncionarios.add(parseRowsToFuncionarioDTOs(row));
            }
        }
        return ListaFuncionarios;
    }

    private FuncionarioDTO parseRowsToFuncionarioDTOs(Row row) {
        FuncionarioDTO funcionario = new FuncionarioDTO();
        funcionario.setNome(row.getCell(0).getStringCellValue());
        funcionario.setMatricula(row.getCell(1).getStringCellValue());
        funcionario.setCargo(row.getCell(2).getStringCellValue());
        funcionario.setDepartamento(row.getCell(3).getStringCellValue());
        funcionario.setDataAdmissao(row.getCell(4).getLocalDateTimeCellValue().toLocalDate());
        funcionario.setSituacao(Enum.valueOf(com.example.projetoRestSpringBoot.enums.FuncionarioSituacao.class, row.getCell(5).getStringCellValue()));
        funcionario.setTipoContrato(Enum.valueOf(com.example.projetoRestSpringBoot.enums.FuncionarioTipoContrato.class, row.getCell(6).getStringCellValue()));
        funcionario.setEmail(row.getCell(7).getStringCellValue());
        funcionario.setTelefone(row.getCell(8).getStringCellValue());

        return funcionario;
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
