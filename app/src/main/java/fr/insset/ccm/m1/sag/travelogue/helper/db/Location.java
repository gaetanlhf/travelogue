package fr.insset.ccm.m1.sag.travelogue.helper.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;

public class Location {

    private final String id;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Location(String id){
        this.id = id;
    }

    public void addPoint(GpsPoint gpsPoint){

        db.collection(id)
                .document("data")
                .collection("travels")
                .orderBy("startDate", Query.Direction.DESCENDING)
                .orderBy("startTime", Query.Direction.DESCENDING)
                .limit(1)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("TEST", document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w("TEST", "Error getting documents.", task.getException());
                    }
                });

    }
}
