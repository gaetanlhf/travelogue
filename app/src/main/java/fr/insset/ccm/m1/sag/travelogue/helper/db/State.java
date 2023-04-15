package fr.insset.ccm.m1.sag.travelogue.helper.db;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

    public void getCurrentTravel(Callback2 callback2){
        AtomicReference<String> currentTravel = new AtomicReference<>();
        db.collection(id)
                .document("state")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(task.getResult().get("currentTravel") != null){
                            currentTravel.set(task.getResult().get("currentTravel").toString());
                        }
                        callback2.onCallback2(currentTravel);
                    }
                });
    }

    public interface Callback {
        void onCallback(AtomicBoolean travelling);
    }

    public interface Callback2 {
        void onCallback2(AtomicReference<String> currentTravel);
    }



}
