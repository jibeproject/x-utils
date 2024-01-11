package org.cam.intervention;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.List;

public class CsvReader {
    public List<String[]> read(String file) {
        List<String[]> data = null;

        try {
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            data = csvReader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
