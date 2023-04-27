package fr.insset.ccm.m1.sag.travelogue.helper.db;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;

import fr.insset.ccm.m1.sag.travelogue.helper.AppSettings;

public class Settings {

    private final String id;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Settings(String id) {
        this.id = id;
    }

    public void isPeriodicTrackingEnable(Callback callback) {
        AtomicReferenceArray<String> atomicReferenceArray = new AtomicReferenceArray<>(2);

        db.collection(id)
                .document("settings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                            atomicReferenceArray.set(0, task.getResult().get("enableAutoGetPoint").toString());
                            atomicReferenceArray.set(1, task.getResult().get("timeBetweenAutoGetPoint").toString());
                        callback.onCallback(atomicReferenceArray);
                    }
                });
    }

    public void getSettings(Callback callback) {
        AtomicReferenceArray<String> settings = new AtomicReferenceArray<>(2);
        db.collection(id)
                .document("settings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        settings.set(0, Objects.requireNonNull(task.getResult().get("enableAutoGetPoint")).toString());
                        settings.set(1, Objects.requireNonNull(task.getResult().get("timeBetweenAutoGetPoint")).toString());
                        callback.onCallback(settings);
                    }
                });
    }

    public void setAutoGps(Boolean value) {
        Map<String, Object> updateSettings = new HashMap<>();
        updateSettings.put("enableAutoGetPoint", value);

        db.collection(id)
                .document("settings")
                .set(updateSettings, SetOptions.merge());

        AppSettings.setAutoGps(value);
    }

    public void setTimeBetweenAutoGps(Integer value) {
        Map<String, Object> updateSettings = new HashMap<>();
        updateSettings.put("timeBetweenAutoGetPoint", value);

        db.collection(id)
                .document("settings")
                .set(updateSettings, SetOptions.merge());

        AppSettings.setTimeBetweenAutoGps(value);
    }

    public interface Callback {
        void onCallback(AtomicReferenceArray atomicReferenceArray);
    }

}
