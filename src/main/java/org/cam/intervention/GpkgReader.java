package org.cam.intervention;

import org.geotools.data.simple.SimpleFeatureReader;
import org.geotools.geometry.jts.JTS;
import org.geotools.geopkg.GeoPackage;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GpkgReader {

    //public String inputFilePath = null;
    // public List<SimpleFeature> listOfSimpleFeatures = new ArrayList<>();
    GpkgReader(){
        //this.inputFilePath = inputFilePath;
        //read_gpkg();
    }

    public List<SimpleFeature> read_gpkg(String inputFilePath){

        List<SimpleFeature> listOfSimpleFeatures = new ArrayList<>();

        try {
            GeoPackage geopkg = new GeoPackage(new File(inputFilePath));
            SimpleFeatureReader r = geopkg.reader(geopkg.features().get(0),null,null);

            //TYPE = r.getFeatureType();

            while (r.hasNext()) {
                SimpleFeature polygon = r.next();
                listOfSimpleFeatures.add(polygon); // todo: is that really useful
                /*
                postcodes.add(new PostCode(
                        transformed.getCentroid().getX(),
                        transformed.getCentroid().getY(),
                        ((Double) polygon.getAttribute("Total")).intValue()));

                 */
            }
            r.close();
            geopkg.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listOfSimpleFeatures;
    }
    public List<SimpleFeature> to_crs(List<SimpleFeature> listOfSimpleFeatures,
                                      CoordinateReferenceSystem targetCRS,
                                      CoordinateReferenceSystem sourceCRS) throws FactoryException, TransformException {

        // Transform coordinates
        //CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:27700");
        //CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");
        if (sourceCRS == null){
            sourceCRS = CRS.decode("EPSG:27700"); // British CRS
        }

        MathTransform mTrans = CRS.findMathTransform(sourceCRS, targetCRS);
        Geometry transformedPolygonGeometry = null;

        for(SimpleFeature sp : listOfSimpleFeatures){
            transformedPolygonGeometry = JTS.transform((Geometry) sp.getAttribute("geom"), mTrans);
            sp.setAttribute("geom", transformedPolygonGeometry);
        }

        //polygon.setAttribute("geom",
        //
        return listOfSimpleFeatures;

    }
}
