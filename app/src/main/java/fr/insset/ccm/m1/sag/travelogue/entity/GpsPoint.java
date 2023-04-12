package fr.insset.ccm.m1.sag.travelogue.entity;

import java.time.Instant;

public class GpsPoint {
    private int ID;
    private final int longitude;
    private final int latitude;


//    public GpsPoint(gps point from Augee) {

    /**
     * GPS Point Constructor
     */
    public GpsPoint() {
        this.longitude = 5;
        this.latitude = 5;
    }

    // GETTERS & ID SETTER

    /**
     *
     * @return
     */
    public int getID() {
        return this.ID;
    }

    /**
     *
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     *
     * @return
     */
    public int getLongitude() {
        return this.longitude;
    }

    /**
     *
     * @return
     */
    public int getLatitude() {
        return this.latitude;
    }
}
