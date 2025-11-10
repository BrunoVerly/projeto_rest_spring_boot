package com.example.projetoRestSpringBoot.file.importer.impl;

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
/**
@Component
public class XlsxImporter implements FileImporter {
    @Override
    public List<PersonDTO> importFile(InputStream inputStream) throws Exception {
        try(XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if(rowIterator.hasNext()){
                rowIterator.next();
            }
            
            return parseRowsToPersonDTOList(rowIterator);
        }
    }

    private List<PersonDTO> parseRowsToPersonDTOList(Iterator<Row> rowIterator) {
        List<PersonDTO> personDTOs = new ArrayList<>();
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
            if(isRowValid(row)){
                personDTOs.add(parseRowsToPersonDTOs(row));
            }
        }
        return personDTOs;
    }

    private PersonDTO parseRowsToPersonDTOs(Row row) {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setFirstName(row.getCell(0).getStringCellValue());
        personDTO.setLastName(row.getCell(1).getStringCellValue());
        personDTO.setAddress(row.getCell(2).getStringCellValue());
        personDTO.setGender(row.getCell(3).getStringCellValue());
        personDTO.setEnabled(true);
        return personDTO;
    }

    private static boolean isRowValid(Row row) {
        return row.getCell(0) != null && row.getCell(0).getCellType() != CellType.BLANK;
    }
}
 **/
