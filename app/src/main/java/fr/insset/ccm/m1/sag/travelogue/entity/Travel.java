package fr.insset.ccm.m1.sag.travelogue.entity;

public class Travel {

    private int ID;
    private String title;
    private Moment startingMoment;
    private Moment endingMoment;
//    private '' userID;


    /**
     * 
     */
    public Travel(String title, Moment startingMoment) {
        this.title = title;
        this.startingMoment = startingMoment;
        this.endingMoment = null;
    }

    // GETTERS & SETTERS

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
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
    }
}
