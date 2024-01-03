package org.cam.gis;

import com.google.common.math.LongMath;

import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureReader;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.geotools.geopkg.GeoPackage;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.FactoryException;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class MapTripAttractionsNeighboursApproach {
    private final static Logger log = Logger.getLogger(MapTripAttractionsNeighboursApproach.class);

    public static void main(String[] args) throws FactoryException {

        //Map<String, SimpleFeature> polygons = new HashMap<>();
        //Map<String, OAitem> OAitems = new HashMap<>();

        List<SimpleFeature> polygons = new ArrayList<>();
        List<OAitem> OAitems = new ArrayList<>();

        Integer i = 0, counter = 0;

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("oas");
        builder.setCRS(CRS.decode("EPSG:27700")); //todo: check CRS manchester
        SimpleFeatureType TYPE = builder.buildFeatureType();

        // Create a Map and store all the polygons as Simplefeature
        try {

            // /Users/ismailsaadi/Cambridge/jibe/destination_choice/coefficients/predicted_attractions_OA.gpkg
            // destination_choice/observed_attractions/attractions_OA.gpkg
            GeoPackage geopkg = new GeoPackage(new File("destination_choice/coefficients/mapped_OA_weighted_sp.gpkg"));
            SimpleFeatureReader r = geopkg.reader(geopkg.features().get(0), null, null);
            TYPE = r.getFeatureType();

            while (r.hasNext()) {
                SimpleFeature polygon = r.next();
                polygons.add(polygon);
                i++;
            }
            r.close();
            geopkg.close();
            i = 0;

        } catch (IOException e) {
            e.printStackTrace();
        }

        double testValue = 0.0, mmax = 0.0;

        // Test all the combinations
        for (SimpleFeature target : polygons) {
            // Create new OA item
            //OAitem oa = new OAitem(target);

            OAitems.add(new OAitem(copySimpleFeature(target)));

            // Get the last element
            int lastIndex = OAitems.size() - 1;

            for (SimpleFeature neighbor : polygons) {
                if (isNeighbor((Geometry) target.getAttribute("geom"), (Geometry) neighbor.getAttribute("geom")) && (!target.getAttribute("OA11CD").toString().equals(neighbor.getAttribute("OA11CD").toString()))) {
                    mmax = (double) neighbor.getAttribute("shop");
                    // log.info(mmax);
                    if (mmax > testValue) {
                        testValue = mmax;
                        log.warn(testValue);
                    }
                    OAitems.get(lastIndex).add(neighbor);
                    i++;
                }
                ;
            }
            OAitems.get(lastIndex).update();
            //OAitems.add(oa);

            counter++;

            /*
            if (counter > 200) {
                break;
            }

             */


            if (LongMath.isPowerOfTwo(counter)) {
                log.info("Processing output area " + counter + " / " + polygons.size());
            }
        }

        // TEST
        /*

        log.info("***************************TEST***************************");
        log.info(OAitems.size());
        for(OAitem f: OAitems){
            //f.neighbours.forEach(e -> System.out.println(e.getAttribute("shop")));
            //f.update();

            log.info("==");
            f.neighbours.forEach(e -> System.out.println(e.getAttribute("shop")));
            log.info(f.columnsToBeAggregated.get("shop"));
            log.info("==");



        }

         */



        // Printing processed OA
        /*
        for (OAitem oa : OAitems.values()) {
            oa.update();
        }*/

        /*

        for (OAitem oa : OAitems.values()) {
            oa.neighbours.forEach(nn -> System.out.println(nn.getAttribute("escort")));
            System.out.println("=======");
        }

         */


        // Write Geopackage
        String outputEdgesFilePath = "destination_choice/coefficients/mapped_OA_weighted_quasiOA.gpkg";
        File outputEdgesFile = new File(outputEdgesFilePath);
        if (outputEdgesFile.delete()) {
            log.warn("File " + outputEdgesFile.getAbsolutePath() + " already exists. Overwriting.");
        }

        final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(); // final -> define an entity that can only be assigned once
        //final SimpleFeatureType TYPE = polygons.get(1).getFeatureType(); polygons.values().
        // final SimpleFeatureType TYPE = createFeatureType();
        final SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
        DefaultFeatureCollection collection = new DefaultFeatureCollection("Polygons", TYPE);

        // Geometry
        for (OAitem polygon : OAitems) {
            //polygon.newOA
            //featureBuilder.add(geometryFactory.createMultiPolygon()); // todo: check how to add polygon
            //featureBuilder.add("EA1");
            //SimpleFeature feature = featureBuilder.buildFeature(null);
            //collection.add(feature);
            collection.add(polygon.getNewFeature());
        }


        try {
            GeoPackage out = new GeoPackage(outputEdgesFile);
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
