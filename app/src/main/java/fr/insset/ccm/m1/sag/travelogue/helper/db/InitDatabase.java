package fr.insset.ccm.m1.sag.travelogue.helper.db;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.insset.ccm.m1.sag.travelogue.Constants;

public class InitDatabase {
    private final String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public InitDatabase(String id) {
        this.id = id;
    }

    public void isInit(Callback callback) {
        AtomicBoolean init = new AtomicBoolean(false);
        db.collection(id)
                .get()
                .addOnCompleteListener(task -> {
                    init.set(task.getResult().size() > 0);
                    callback.onCallback(init);
                });
    }

    public void initDb() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("enableAutoGetPoint", false);
        settings.put("timeBetweenAutoGetPoint", 5);

        Map<String, Object> state = new HashMap<>();
        state.put(Constants.DRIVE_FOLDER_DATABASE_KEY, "");
        state.put("isTravelling", false);
        state.put("currentTravel", null);

        db.collection(id)
                .document("settings")
                .set(settings);

        db.collection(id)
                .document("state")
                .set(state);

        db.collection(id)
                .document("travels");
    }

    public interface Callback {
        void onCallback(AtomicBoolean init);
    }
}
