package org.cam.intervention;

import java.util.Random;

public class RandomHouseholdsAndFacilities {

    static class Point {
        double x, y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // Generate an array of n random points with x/y coordinates
    public static Point[] generateRandomPoints(int n) {
        Point[] points = new Point[n];
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            double x = rand.nextDouble() * 100; // Adjust the range as needed
            double y = rand.nextDouble() * 100; // Adjust the range as needed
            points[i] = new Point(x, y);
        }

        return points;
    }

    // Build a distance matrix based on Euclidean distance between households and facilities
    public static double[][] buildDistanceMatrix(Point[] households, Point[] facilities) {
        int n = households.length;
        int m = facilities.length;
        double[][] distanceMatrix = new double[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                distanceMatrix[i][j] = calculateEuclideanDistance(households[i], facilities[j]);
            }
        }

        return distanceMatrix;
    }

    // Calculate Euclidean distance between two points
    public static double calculateEuclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }
}

