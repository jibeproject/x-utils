package org.cam.io;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.cam.io.Trip;

public class ReadCSV {
    private static Logger logger = LogManager.getLogger(ReadCSV.class);

    public static void main(String[] arg){
        String[] header = null;
        List<Trip> allTrips = new ArrayList<>();
        List<String[]> allData = new ArrayList<>();

        // Read trip data
        try {
            FileReader filereader = new FileReader("travelSurvey/tripsWithXY.csv");

            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(filereader).withCSVParser(parser).build();
            header = csvReader.readNext();

            logger.info("Reading input file");
            allData = csvReader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Print header
        /*
        for(String cell : header){
            System.out.println(cell);
        }
         */

        // loop over the trips
        for (String[] row : allData) {
            //new Trip()
            allTrips.add(new Trip(
                    getVariableValue("IDNumber", header, row),
                    Double.parseDouble(getVariableValue("StartEasting", header, row)),
                    Double.parseDouble(getVariableValue("StartNorthing", header, row)),
                    Double.parseDouble(getVariableValue("EndEasting", header, row)),
                    Double.parseDouble(getVariableValue("EndNorthing", header, row))));
        }

        for(Trip trip : allTrips){
            //logger.info(trip.orgX);
            // System.out.println(trip.orgX);
        }
    }

    public static String getVariableValue(String variable_name, String[] header, String[] line){
        int cpt=0;
        for(String label : header){
            if(label.equals(variable_name)){
                break;
            }
            cpt++;
        }
        if (line[cpt].equals("NA")){
            logger.warn("NA value found in " + variable_name);
            return "-1.0"; // meaning that it's NAN
        }else{
            return line[cpt];
        }

    }
}
