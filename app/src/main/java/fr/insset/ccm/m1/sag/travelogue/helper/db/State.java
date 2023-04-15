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
                            travelling.set(task.getResult().get("isTravelling").toString() == "true");
                            callback.onCallback(travelling);
                        }
                    }
                });
    }

<<<<<<< HEAD
    public void getCurrentTravel(Callback2 callback2) {
=======
    public void getCurrentTravel(Callback2 callback2){
>>>>>>> 32e34aa (feat: track travel)
        AtomicReference<String> currentTravel = new AtomicReference<>();
        db.collection(id)
                .document("state")
                .get()
                .addOnCompleteListener(task -> {
<<<<<<< HEAD
                    if (task.isSuccessful()) {
                        if (task.getResult().get("currentTravel") != null) {
=======
                    if(task.isSuccessful()){
                        if(task.getResult().get("currentTravel") != null){
>>>>>>> 32e34aa (feat: track travel)
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
<<<<<<< HEAD
=======

>>>>>>> 32e34aa (feat: track travel)


}
