package fr.insset.ccm.m1.sag.travelogue.helper.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import fr.insset.ccm.m1.sag.travelogue.helper.AppSettings;

public class State {
    private final String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public State(String id) {
        this.id = id;
    }

    public void isTravelling(Callback callback) {
        AtomicBoolean travelling = new AtomicBoolean(false);
        db.collection(id)
                .document("state")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        travelling.set(Boolean.parseBoolean(task.getResult().get("isTravelling").toString()));
                        callback.onCallback(travelling);
                    }
                });
    }

    public void getCurrentTravel(Callback2 callback2) {
        AtomicReference<String> currentTravel = new AtomicReference<>();
        db.collection(id)
                .document("state")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().get("currentTravel") != null) {
                            currentTravel.set(task.getResult().get("currentTravel").toString());
                        }
                        callback2.onCallback2(currentTravel);
                    }
                });
    }

    public void setTravelling(Boolean value) {
        Map<String, Object> updateState = new HashMap<>();
        updateState.put("isTravelling", value);
        if (!value) {
            updateState.put("currentTravel", null);
        }

        db.collection(id)
                .document("state")
                .set(updateState, SetOptions.merge());

        AppSettings.setTravelling(value);
    }

    public interface Callback {
        void onCallback(AtomicBoolean travelling);
    }

    public interface Callback2 {
        void onCallback2(AtomicReference<String> currentTravel);
    }

}
