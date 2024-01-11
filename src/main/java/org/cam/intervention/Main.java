package org.cam.intervention;

import com.google.common.math.LongMath;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.geotools.data.simple.SimpleFeatureReader;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geopkg.GeoPackage;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.w3.xlink.Simple;

public class Main {

    private final static Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) throws FactoryException, TransformException {

        // Read file with origins (including population estimates)
        String inputFilePath = "/Users/ismailsaadi/Cambridge/jibe/network/manchester/postcode_area.gpkg";
        String inputFilePath2 = "/Users/ismailsaadi/Cambridge/jibe/destination_choice/poi/Eating_establishments.csv";

        //
        GpkgReader gpkgReader = new GpkgReader();
        List<SimpleFeature> features = gpkgReader.read_gpkg(inputFilePath);
        features = gpkgReader.to_crs(
                features,
                CRS.decode("EPSG:3857"), // target
                CRS.decode("EPSG:27700")); // source

        //List<SimpleFeature> features = new ArrayList<>();
        List<PostCode> postcodes = new ArrayList<>();
        List<PointOfInterest> pois = new ArrayList<>();
        int i = 0;

        // To transform SimpleFeatures to postcodes
        for (SimpleFeature feature : features) {
            postcodes.add(new PostCode(
                    ((Geometry) feature.getAttribute("geom")).getCentroid().getX(),
                    ((Geometry) feature.getAttribute("geom")).getCentroid().getY(),
                    ((Double) feature.getAttribute("Total")).intValue()));
        }

        // todo: write a script to generate SimpleFeatures from csv data (to be consistent with origins)

        // To build the destinations
        CsvReader csvReader = new CsvReader();
        List<String[]> lines = csvReader.read(inputFilePath2);
        String[] elements = null;
        CoordinateReferenceSystem crs = CRS.decode("EPSG:27700");
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        Point point = null;
        Point transformedPoint = null;
        MathTransform mTrans = CRS.findMathTransform(CRS.decode("EPSG:27700"), CRS.decode("EPSG:3857"));

        for (String[] row : lines) {
            for (String cell : row) {
                elements = cell.split(";");
                if (elements.length == 6) {
                    point = geometryFactory.createPoint(new Coordinate(Double.parseDouble(elements[1]), Double.parseDouble(elements[2])));
                    point.setUserData(crs); // todo: may be put this elsewhere
                    transformedPoint = (Point) JTS.transform(point, mTrans);

                    pois.add(new PointOfInterest(
                            transformedPoint.getX(),
                            transformedPoint.getY(),
                            (String) elements[5]));
                }
            }
        }

        // To keep OD pairs which are within bee-distance threshold
        Calculator calculator = new Calculator();
        List<Double> distances = calculator.run(postcodes, pois);

        // Write
        log.info("Writing ...");
        CsvWriter csvWriter = new CsvWriter();
        csvWriter.write("distances.csv", transformDoubleListToStringList(distances));

        // todo: fix this
        //log.info("Number of edges: " + k);
        //log.info("Max number of edges: " + postcodes.size() * pois.size());
        //log.info("Ratio: " + 100 * ((double) k) / ((double) postcodes.size() * pois.size()));

        //
        log.info("Clearing ...");
        postcodes.clear();
        pois.clear();
        features.clear();

        log.info("SHUTDOWN");
    }

    public static List<String[]> transformDoubleListToStringList(List<Double> doubleList) {
        List<String[]> stringArrayList = doubleList.stream()
                .map(doubleValue -> new String[]{doubleValue.toString()})
                .collect(java.util.stream.Collectors.toList());

        return stringArrayList;
    }
}