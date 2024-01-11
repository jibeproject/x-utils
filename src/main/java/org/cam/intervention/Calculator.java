package org.cam.intervention;

import com.google.common.math.LongMath;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    List<Double> distances = new ArrayList<>();
    final double averageWalkingSpeed = 4.82; // km/h
    int k = 0, counter = 0;

    public List<Double> run(List<PostCode> postcodes, List<PointOfInterest> pois){
        double dstanceOD = 0.0;

        // Here I perform the pairwise comparison between origins and destinations
        for (PostCode pc : postcodes) {
            for (PointOfInterest poi : pois) {
                dstanceOD = calculateDistanceBetweenPoints(pc.x, pc.y, poi.x, poi.y);
                if (dstanceOD < this.averageWalkingSpeed / 3. * 1000 && pc.popsize > 0) {
                    this.distances.add(dstanceOD);
                    k++;
                }
            }
            /*
            if (LongMath.isPowerOfTwo(counter)) {
                log.info("Processing output area " + counter + " / " + postcodes.size());
            } else if (counter == (postcodes.size() - 1)) {
                log.info("Processing output area " + postcodes.size() + " / " + postcodes.size());
            }
             */
            // todo: fix the logger
            //counter++;
        }
        return distances;
    }
    public double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

}
