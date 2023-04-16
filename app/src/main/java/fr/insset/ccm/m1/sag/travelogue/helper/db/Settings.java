package fr.insset.ccm.m1.sag.travelogue.helper.db;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReferenceArray;

public class Settings {

    private final String id;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Settings(String id){
        this.id = id;
    }

    public void isPeriodicTrackingEnable(Callback callback){
        AtomicReferenceArray<String> atomicReferenceArray = new AtomicReferenceArray<>(2);

        db.collection(id)
                .document("settings")
                .get()
                .addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
                    if(task.isSuccessful()){
                        if(task.getResult().get("enableAutoGetPoint").toString().equals("true")){
                            atomicReferenceArray.set(0,"true");
                            atomicReferenceArray.set(1,task.getResult().get("timeBetweenAutoGetPoint").toString());
                        }
                        callback.onCallback(atomicReferenceArray);
                    }
                });
    }

    public interface Callback{
        void onCallback(AtomicReferenceArray atomicReferenceArray);
    }

}
