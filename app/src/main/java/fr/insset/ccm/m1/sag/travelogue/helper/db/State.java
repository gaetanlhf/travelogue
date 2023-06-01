package fr.insset.ccm.m1.sag.travelogue.helper.db;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedPrefManager;

public class State {
    private final String id;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPrefManager sharedPrefManager;

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

    public void setTravelling(Context context, Boolean value) {
        Map<String, Object> updateState = new HashMap<>();
        updateState.put("isTravelling", value);
        if (!value) {
            updateState.put("currentTravel", null);
        }

        db.collection(id)
                .document("state")
                .set(updateState, SetOptions.merge());

        sharedPrefManager = SharedPrefManager.getInstance(context);
        sharedPrefManager.updateBool("Travelling", value);
    }

    public void getTravelogueFolderId(CallbackTravelogueFolder callback) {
        AtomicReference<String> travelogueFolderId = new AtomicReference<>("");
        db.collection(id)
                .document("state")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().get(Constants.DRIVE_FOLDER_DATABASE_KEY) != null) {
                            travelogueFolderId.set(task.getResult().get(Constants.DRIVE_FOLDER_DATABASE_KEY).toString());
                        }
                        callback.onCallbackTravelogueFolder(travelogueFolderId);
                    }
                });
    }

    public void setTravelogueFolderId(Context context, String value) {
        AtomicReference<String> travelogueFolderId = new AtomicReference<>(value);
        if (value == null) {
            travelogueFolderId.set("");
        }

        db.collection(id)
                .document("state")
                .update(Constants.DRIVE_FOLDER_DATABASE_KEY, travelogueFolderId.get());

        sharedPrefManager = SharedPrefManager.getInstance(context);
        sharedPrefManager.updateString(Constants.DRIVE_FOLDER_DATABASE_KEY, travelogueFolderId.get());
    }

    public interface Callback {
        void onCallback(AtomicBoolean travelling);
    }

    public interface Callback2 {
        void onCallback2(AtomicReference<String> currentTravel);
    }

    public interface CallbackTravelogueFolder {
        void onCallbackTravelogueFolder(AtomicReference<String> travelogueFolderId);
    }

}
