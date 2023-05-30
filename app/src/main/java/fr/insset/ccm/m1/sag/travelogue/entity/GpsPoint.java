package fr.insset.ccm.m1.sag.travelogue.entity;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class GpsPoint {
    private double longitude;
    private double latitude;
    private String linkedData;
    private String linkedDataType;
    private String timestamp;

    /**
     * GPS Point Constructor
     */
    public GpsPoint(double longitude, double latitude, String timestamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
    }

    public GpsPoint(double longitude, double latitude, String linkedDataType, String linkedData) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.linkedData = linkedData;
        this.linkedDataType = linkedDataType;
    }
    public GpsPoint(double longitude, double latitude, String linkedDataType, String linkedData, String timestamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.linkedData = linkedData;
        this.linkedDataType = linkedDataType;
        this.timestamp = timestamp;
    }

    public String getLinkedData() {
        return linkedData;
    }


//    public GpsPoint()  Check with Augustin

    public void setLinkedData(String linkedData) {
        this.linkedData = linkedData;
    }

    public String getLinkedDataType() {
        return linkedDataType;
    }

    public void setLinkedDataType(String linkedDataType) {
        this.linkedDataType = linkedDataType;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        Gson gpsJson = new Gson();
        return gpsJson.toJson(this);
    }

}
