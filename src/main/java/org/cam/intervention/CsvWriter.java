package org.cam.intervention;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvWriter {
    public void write(String filePath, List<String[]> data) {
        File file = new File(filePath);
        List<String[]> dataf = new ArrayList<String[]>();
        dataf.add(new String[]{"dist"});

        try {
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            data.forEach(x -> dataf.add(x));
            writer.writeAll(dataf);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
