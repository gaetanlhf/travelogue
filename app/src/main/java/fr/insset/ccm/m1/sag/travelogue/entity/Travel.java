package fr.insset.ccm.m1.sag.travelogue.entity;

import com.google.gson.Gson;

import androidx.annotation.NonNull;
import java.util.List;

public class Travel {

    private Integer ID;
    private String title;

    private String startDate;

    private String startTime;

    /**
     * 
     */
    public Travel(Integer id, String title, String startDate, String startTime) {
        this.ID = id;
        this.title = title;
        this.startDate = startDate;
        this.startTime = startTime;
    }

    public Travel(String title, String startDate, String startTime) {
        this.title = title;
        this.startDate = startDate;
        this.startTime = startTime;
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

    public Integer getID() {
        return this.ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

}
