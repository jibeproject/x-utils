package org.cam.io;

public class Trip{
    String tripID = null;
    double orgX = 0.0;
    double orgY = 0.0;
    double destX = 0.0;
    double destY = 0.0;

    public Trip(String tripID, double orgX, double orgY, double destX, double destY){
        this.tripID = tripID;
        this.orgX = orgX;
        this.orgY = orgY;
        this.destX = destX;
        this.destY = destY;
    }
}
