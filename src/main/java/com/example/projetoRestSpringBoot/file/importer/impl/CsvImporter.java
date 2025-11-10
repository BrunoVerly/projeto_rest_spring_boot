package com.example.projetoRestSpringBoot.file.importer.impl;

import com.example.projetoRestSpringBoot.file.importer.contract.FileImporter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
@Component
public class CsvImporter implements FileImporter {
    @Override
    public List<PersonDTO> importFile(InputStream inputStream) throws Exception {
        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(new InputStreamReader(inputStream));

        return parseRecordsToPersonDTOs(records);
    }

    private List<PersonDTO> parseRecordsToPersonDTOs(Iterable<CSVRecord> records) {
        List<PersonDTO> personDTOs = new ArrayList<>();
        for (CSVRecord record : records) {
            PersonDTO personDTO = new PersonDTO();
            personDTO.setFirstName(record.get("first_name"));
            personDTO.setLastName(record.get("last_name"));
            personDTO.setAddress(record.get("address"));
            personDTO.setGender(record.get("gender"));
            personDTO.setEnabled(true);
            personDTOs.add(personDTO);
        }
        return personDTOs;
    }
}
**/