package fr.insset.ccm.m1.sag.travelogue.entity;

public class Travel {

    private String ID;
    private String title;

    private String endTimestamp;

    private boolean isFinish;

    /**
     *
     */
    public Travel(String id, String title, String endTimestamp, boolean isFinish) {
        this.ID = id;
        this.title = title;
        this.endTimestamp = endTimestamp;
        this.isFinish = isFinish;
    }

    public Travel(String id, String title, String endTimestamp) {
        this.ID = id;
        this.title = title;
        this.endTimestamp = endTimestamp;
    }

    public Travel(String title) {
        this.title = title;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public String getID() {
        return this.ID;
    }


    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
