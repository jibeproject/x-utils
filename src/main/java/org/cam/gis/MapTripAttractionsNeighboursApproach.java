package org.cam.gis;

import com.google.common.math.LongMath;

import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureReader;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.geotools.geopkg.GeoPackage;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MapTripAttractionsNeighboursApproach {
    private final static Logger log = Logger.getLogger(MapTripAttractionsNeighboursApproach.class);

    public static void main(String[] args) throws FactoryException {

        List<SimpleFeature> polygons = new ArrayList<>();
        List<OAitem> OAitems = new ArrayList<>();

        String InputFilePath = "destination_choice/coefficients/mapped_OA_weighted_sp.gpkg";
        String outputFilePath = "destination_choice/coefficients/mapped_OA_weighted_quasiOA.gpkg";

        int counter = 0, lastIndex;

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("oas");
        builder.setCRS(CRS.decode("EPSG:27700")); //todo: check CRS manchester
        SimpleFeatureType TYPE = builder.buildFeatureType();

        // Create a Map and store all the polygons as Simplefeature
        try {
            GeoPackage geopkg = new GeoPackage(new File(InputFilePath));
            SimpleFeatureReader r = geopkg.reader(geopkg.features().get(0), null, null);
            TYPE = r.getFeatureType();

            while (r.hasNext()) {
                SimpleFeature polygon = r.next();
                polygons.add(polygon);
            }
            r.close();
            geopkg.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Test all the combinations
        for (SimpleFeature target : polygons) {
            OAitems.add(new OAitem(copySimpleFeature(target)));

            // Get the last element
            lastIndex = OAitems.size() - 1;

            for (SimpleFeature neighbor : polygons) {
                if (isNeighbor((Geometry) target.getAttribute("geom"), (Geometry) neighbor.getAttribute("geom")) && (!target.getAttribute("OA11CD").toString().equals(neighbor.getAttribute("OA11CD").toString()))) {
                    OAitems.get(lastIndex).add(neighbor);
                }
            }
            OAitems.get(lastIndex).update();
            counter++;

            if (LongMath.isPowerOfTwo(counter)) {
                log.info("Processing output area " + counter + " / " + polygons.size());
            }
        }

        // Write Geopackage
        File outputFile = new File(outputFilePath);
        if (outputFile.delete()) {
            log.warn("File " + outputFile.getAbsolutePath() + " already exists. Overwriting.");
        }

        DefaultFeatureCollection collection = new DefaultFeatureCollection("Polygons", TYPE);

        // Geometry
        for (OAitem polygon : OAitems) {
            collection.add(polygon.getNewFeature());
        }

        try {
            GeoPackage out = new GeoPackage(outputFile);
            out.init();
            FeatureEntry entry = new FeatureEntry();
            entry.setDescription("network");
            out.add(entry, collection);
            out.createSpatialIndex(entry);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // clear all
        OAitems.clear();
        polygons.clear();
    }

    /*
    private static SimpleFeatureType createFeatureType() throws FactoryException {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("oas");
        builder.setCRS(CRS.decode("EPSG:27700")); //todo: check CRS manchester

        // add attributes
        builder.add("OA11CD", String.class);
        builder.add("LSOA11CD", String.class);
        builder.add("MSOA11CD", String.class);
        builder.add("escort", Double.class);
        builder.add("shop", Double.class);
        builder.add("recreationx", Double.class);
        //builder.add("recreation.x", Double.class);
        builder.add("other", Double.class);
        builder.add("HBE", Double.class);
        builder.add("HBS", Double.class);
        builder.add("HBR", Double.class);
        builder.add("HBO", Double.class);
        builder.add("NHBO", Double.class);
        builder.add("NHBW", Double.class);
        builder.add("early_year_access", Double.class);
        builder.add("eating_establishments", Double.class);
        builder.add("financial", Double.class);
        builder.add("food_retail", Double.class);
        builder.add("public_open_space", Double.class);
        //builder.add("recreation.y", Double.class);
        builder.add("education", Double.class);
        builder.add("social_and_culture", Double.class);
        builder.add("services", Double.class);
        builder.add("primary_health_care", Double.class);
        builder.add("community_health", Double.class);
        builder.add("geom", MultiPolygon.class);
        builder.add("recreationy", Double.class);

        return builder.buildFeatureType();
    }
     */
    public static boolean isNeighbor(Geometry target, Geometry neighbor) {
        return target.touches(neighbor);
    }

    public static SimpleFeature copySimpleFeature(SimpleFeature original) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(original.getFeatureType());
        featureBuilder.addAll(original.getAttributes());
        return featureBuilder.buildFeature(original.getID());
    }
}
