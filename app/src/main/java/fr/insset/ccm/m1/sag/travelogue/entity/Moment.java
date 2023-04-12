package fr.insset.ccm.m1.sag.travelogue.entity;

import java.time.Instant;

/**
 * Class Moment
 */
public class Moment {

    private int ID;
    private Instant savingDate;
    private String caption;
    private String imageURL;
    private int travelID;
//    private '' gpsPoint;
    private GpsPoint gpsPoint;

    /**
     * Moment constructor
     * @param momentBuilder
     */
    private Moment(MomentBuilder momentBuilder) {
        this.savingDate = momentBuilder.savingDate;
        this.travelID = momentBuilder.travelID;
        this.imageURL = momentBuilder.imageURL;
        this.caption = momentBuilder.caption;
        this.gpsPoint = momentBuilder.gpsPoint;
    }

    // Moment builder
    public static class MomentBuilder {
        private Instant savingDate;
        private String caption;
        private String imageURL;
        private int travelID;

//        private '' gpsPoint;
        private GpsPoint gpsPoint;

        /**
         * MomentBuilder Constructor
         * @param travelID
         */
//        public MomentBuilder(int travelID, '' gpsPoint) {
        public MomentBuilder(int travelID, GpsPoint gpsPoint) {
            this.savingDate = Instant.now();
            this.travelID = travelID;
             this.gpsPoint = gpsPoint;
        }

        /**
         *
         * @param caption
         * @return
         */
        public MomentBuilder setCaption(String caption) {
            this.caption = caption;
            return this;
        }

        /**
         *
         * @param imageURL
         * @return
         */
        public MomentBuilder setImageURL(String imageURL) {
            this.imageURL = imageURL;
            return this;
        }

        /**
         *
         * @return
         */
        public Moment build() {
            return new Moment(this);
        }
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
    public Instant getSavingDate() {
        return this.savingDate;
    }

    /**
     *
     * @return
     */
    public String getCaption() {
        return this.caption;
    }

    /**
     *
     * @return
     */
    public String getImageURL() {
        return this.imageURL;
    }

    /**
     *
     * @return
     */
    public int getTravelID() {
        return this.travelID;
    }

    /**
     *
     * @return
     */
//    public '' getGpsPoint() {
    public GpsPoint getGpsPoint() {
        return this.gpsPoint;
    }
}
