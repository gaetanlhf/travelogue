package fr.insset.ccm.m1.sag.travelogue.entity;

public class MainActivityMethods {
    private static int autoSaveMomentTimer = 30;

    public int getAutoSaveMomentTimer() {
        return MainActivityMethods.autoSaveMomentTimer;
    }

    public static void setAutoSaveMomentTimer(int autoSaveMomentTimer) {
        MainActivityMethods.autoSaveMomentTimer = autoSaveMomentTimer;
    }

    public static Travel startTravel(String title, GpsPoint gpsPoint) {
        Moment moment = new Moment.MomentBuilder(gpsPoint).build();
        return new Travel(title, moment);
    }

    public static void endTravel(Integer travelID, GpsPoint gpsPoint) {
        // Moment moment = new Moment.MomentBuilder(gpsPoint).build();
        // Travel travel = database.get(travelID);
        // travel.setEndingMoment(moment);
    }
}
