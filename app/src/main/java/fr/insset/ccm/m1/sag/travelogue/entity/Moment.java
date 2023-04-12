package fr.insset.ccm.m1.sag.travelogue.entity;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Class Moment
 */
public class Moment {

    private Integer ID;
    private Instant savingDate;
    private String caption;
    private String imageURL;
    private Integer travelID;
    //    private '' gpsPoint;
    private GpsPoint gpsPoint;

    private static final String PATTERN_FORMAT = "dd.MM.yyyy, HH:mm:ss";

    /**
     * Moment constructor
     *
     * @param momentBuilder
     */
    private Moment(MomentBuilder momentBuilder) {
        this.savingDate = momentBuilder.savingDate;
        this.imageURL = momentBuilder.imageURL;
        this.caption = momentBuilder.caption;
        this.gpsPoint = momentBuilder.gpsPoint;
    }

    @NonNull
    @Override
    public String toString() {
        Gson momentJson = new Gson();
        return momentJson.toJson(this);
    }

    // Moment builder
    public static class MomentBuilder {
        private Instant savingDate;
        private String caption;
        private String imageURL;

        //        private '' gpsPoint;
        private GpsPoint gpsPoint;

        /**
         * MomentBuilder Constructor
         *
         * @param gpsPoint
         */
//        public MomentBuilder(int travelID, '' gpsPoint) {
        public MomentBuilder(GpsPoint gpsPoint) {
            this.savingDate = Instant.now();
            this.gpsPoint = gpsPoint;
        }

        /**
         * @param caption
         * @return
         */
        public MomentBuilder setCaption(String caption) {
            this.caption = caption;
            return this;
        }

        /**
         * @param imageURL
         * @return
         */
        public MomentBuilder setImageURL(String imageURL) {
            this.imageURL = imageURL;
            return this;
        }

        /**
         * @return
         */
        public Moment build() {
            return new Moment(this);
        }
    }

    // GETTERS & ID SETTER

    /**
     * @return
     */
    public Integer getID() {
        return this.ID;
    }

    /**
     * @param ID
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * @return
     */
    public Instant getSavingDate() {
        return this.savingDate;
    }

    /**
     * @return
     */
    public String getSavingDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT).withZone(ZoneId.systemDefault());
        return formatter.format(this.savingDate);
    }

    /**
     * @return
     */
    public String getCaption() {
        return this.caption;
    }

    /**
     * @return
     */
    public String getImageURL() {
        return this.imageURL;
    }

    /**
     * @return
     */
    public Integer getTravelID() {
        return this.travelID;
    }

    /**
     * @param travelID
     */
    public void setTravelID(Integer travelID) {
        this.travelID = travelID;
    }

    /**
     * @return
     */
//    public '' getGpsPoint() {
    public GpsPoint getGpsPoint() {
        return this.gpsPoint;
    }
}
