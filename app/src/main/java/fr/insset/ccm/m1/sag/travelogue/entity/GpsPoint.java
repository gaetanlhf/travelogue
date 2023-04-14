package fr.insset.ccm.m1.sag.travelogue.entity;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.gson.Gson;

import java.time.Instant;

public class GpsPoint {
    private static Integer ID = 0;
    private double longitude;
    private double latitude;
    private String hash;


//    public GpsPoint()  Check with Augustin

    /**
     * GPS Point Constructor
     */
    public GpsPoint(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(this.latitude, this.longitude));

        GpsPoint.ID ++;
    }

    @NonNull
    @Override
    public String toString() {
        Gson gpsJson = new Gson();
        return gpsJson.toJson(this);
    }

    // GETTERS & ID SETTER

    /**
     *
     * @return
     */
    public Integer getID() {
        return GpsPoint.ID;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
//    /**
//     *
//     * @param ID
//     */
//    public void setID(Integer ID) {
//        GpsPoint.ID = ID;
//    }

    /**
     *
     * @return
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     *
     * @return
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     *
     * @return
     */
    public String getHash() {
        return this.hash;
    }
}
