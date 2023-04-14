package fr.insset.ccm.m1.sag.travelogue.db;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicBoolean;

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
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                                if (task.getResult().get("isTravelling").toString() == "true") {
                                    travelling.set(true);
                                } else {
                                    travelling.set(false);
                                }
                            callback.onCallback(travelling);
                        }
                    }
                });
    }

    public interface Callback {
        void onCallback(AtomicBoolean travelling);
    }



}
