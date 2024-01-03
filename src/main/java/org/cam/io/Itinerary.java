package org.cam.io;

import com.opencsv.bean.CsvBindByPosition;

public class Itinerary {
    public String idNumber;

    public double personNumber;

    public double tripNumber;

    public double itineraryNumber;

    public double duration;

    public double walkTime;

    public double transitTime;

    public double waitingTime;

    public double walkDistance;

    public int transfers;

    public Itinerary(String idNumber,
                     int personNumber,
                     int tripNumber,
                     int itineraryNumber,
                     double duration,
                     double walkTime,
                     double transitTime,
                     double waitingTime,
                     double walkDistance,
                     int transfers) {

        this.idNumber = idNumber;
        this.personNumber = personNumber;
        this.tripNumber = tripNumber;
        this.itineraryNumber = itineraryNumber;
        this.duration = duration;
        this.walkTime = walkTime;
        this.transitTime = transitTime;
        this.waitingTime = waitingTime;
        this.walkDistance = walkDistance;
        this.transfers = transfers;
    }
}

