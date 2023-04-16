package fr.insset.ccm.m1.sag.travelogue.entity;

import com.google.gson.Gson;

import androidx.annotation.NonNull;
import java.util.List;

public class Travel {

    private Integer ID;
    private String title;
    private Moment startingMoment;
    private Moment endingMoment;
//    private '' userID;

    private List<Moment> moments;

    private boolean canAddMoment;


    /**
     * 
     */
    public Travel(String title, Moment startingMoment) {
        this.title = title;
        this.startingMoment = startingMoment;
        this.addMoment(this.startingMoment);
        this.endingMoment = null;
        this.canAddMoment = true;
    }

    public Travel(String title) {
        this.title = title;
        this.startingMoment = null;
        this.endingMoment = null;
        this.canAddMoment = true;
    }

    public void addMoment(Moment moment) {
        if(this.canAddMoment) {
            if(!this.moments.contains(moment)) {
                if(moment.getTravelID() == null) {
                    moment.setTravelID(this.ID);
                    this.moments.add(moment);
                }
            }
        }
    }

    private void stopAddingMoments() {
        this.canAddMoment = false;
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

    public Moment getStartingMoment() {
        return this.startingMoment;
    }

    public Moment getEndingMoment() {
        return this.endingMoment;
    }

    public void setEndingMoment(Moment endingMoment) {
        this.endingMoment = endingMoment;
        this.addMoment(this.endingMoment);
        this.stopAddingMoments();
    }

    public List<Moment> getMoments() {
        return this.moments;
    }
}
