package fr.insset.ccm.m1.sag.travelogue.entity;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class Travel {

    private String ID;
    private String title;

    private String startDate;

    private String startTime;

    private String endDate;

    private String endTime;

    private boolean isFinish;

    /**
     *
     */
    public Travel(String id, String title, String startDate, String startTime, String endDate, String endTime, boolean isFinish) {
        this.ID = id;
        this.title = title;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.isFinish = isFinish;
    }

    public Travel(String title, String startDate, String startTime, String endDate, String endTime, boolean isFinish) {
        this.title = title;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.isFinish = isFinish;
    }

    public Travel(String id, String title) {
        this.ID = id;
        this.title = title;
    }

    public Travel(String title) {
        this.title = title;
    }

    @NonNull
    @Override
    public String toString() {
        Gson travelJson = new Gson();

//        return "Travel{" + "\n\t" +
//                "ID=" + ID + ",\n\t" +
//                "Title='" + title + "',\n\t" +
//                "StartingMoment=" + startingMoment + ",\n\t" +
//                "EndingMoment=" + endingMoment + ",\n\t" +
//                "Moments=" + moments + ",\n\t" +
//                "CanAddMoment=" + canAddMoment + "\n" +
//                '}';

        return travelJson.toJson(this);
    }

    // GETTERS & SETTERS

    public String getStartDatetime() {
        return this.startDate + " - " + this.startTime;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public String getEndDatetime() {
        return this.endDate + " - " + this.endTime;
    }


    public String getID() {
        return this.ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
