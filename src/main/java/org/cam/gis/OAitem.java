package org.cam.gis;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.util.Cloneable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAitem {
    //Map<String, > neighbours = new HashMap<>();
    List<SimpleFeature> neighbours = new ArrayList<>();

    //String currentOaName;

    SimpleFeature currentOA;

    SimpleFeature newOA;

    Map<String, Double> columnsToBeAggregated = new HashMap<>();

    OAitem(SimpleFeature currentOa) {
        this.currentOA = currentOa;
        this.newOA = currentOa;
        //this.currentOaName = currentOa.getAttribute("OA11CD").toString(); // todo: is this useful ?

        // poi todo: use a loop instead
        columnsToBeAggregated.put("early_year_access", 0.0);
        columnsToBeAggregated.put("eating_establishments", 0.0);
        columnsToBeAggregated.put("financial", 0.0);
        columnsToBeAggregated.put("food_retail", 0.0);
        columnsToBeAggregated.put("public_open_space", 0.0);
        columnsToBeAggregated.put("recreationy", 0.0);
        columnsToBeAggregated.put("education", 0.0);
        columnsToBeAggregated.put("social_and_culture", 0.0);
        columnsToBeAggregated.put("services", 0.0);
        columnsToBeAggregated.put("primary_health_care", 0.0);
        columnsToBeAggregated.put("community_health", 0.0);

        // trip attractions
        columnsToBeAggregated.put("escort", 0.0);
        columnsToBeAggregated.put("shop", 0.0);
        columnsToBeAggregated.put("other", 0.0);
        columnsToBeAggregated.put("recreationx", 0.0);

        //
            /*
            columnsToBeAggregated.put("HBE", 0.0);
            columnsToBeAggregated.put("HBS", 0.0);
            columnsToBeAggregated.put("HBO", 0.0);
            columnsToBeAggregated.put("HBR", 0.0);
            columnsToBeAggregated.put("NHBW", 0.0);
            columnsToBeAggregated.put("NHBO", 0.0);

             */

        // update();
    }

    void add(SimpleFeature featureOA) {

        this.neighbours.add(featureOA);

            /*
            int lastIndex = this.neighbours.size() - 1;
            if(featureOA.getAttribute("shop") != this.neighbours.get(lastIndex).getAttribute("shop") ){
                log.warn("mismatch " + featureOA.getAttribute("OA11CD"));
            }

             */

    }

    // This method aggregates the values from the neighbours
    void aggregate(String label) {
        double cnt = 0.0, tmp2 = 0.0;

            /*

            log.info(this.currentOA.getAttribute("OA11CD") + " has " + this.neighbours.size() + " neighbour(s) | " + label);
            for (SimpleFeature neighbour : this.neighbours) {
                System.out.println(neighbour.getAttribute(label));
            }

             */

        for (SimpleFeature sf : this.neighbours) {
            cnt = cnt + ((double) sf.getAttribute(label));
        }

        //
        tmp2 = (double) this.currentOA.getAttribute(label);

        // update OA's own value
        this.columnsToBeAggregated.put(label, this.columnsToBeAggregated.get(label) + tmp2 + cnt);
    }

    // Update the new OA with the aggregated values / quasi OA value
    void update() {
        for (String key : this.columnsToBeAggregated.keySet()) {
            aggregate(key);
            this.newOA.setAttribute(key, this.columnsToBeAggregated.get(key));
        }
    }

    SimpleFeature getNewFeature() {
        return this.newOA;
    }
}
