package fr.insset.ccm.m1.sag.travelogue.helper.db;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Users {

    private final static String USERS_COLLECTION = "users";
    private final static String AUTH_CODE_TITLE = "authCode";
    private final static String ALBUM_CREATED_TITLE = "albumCreated";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Users(){}

    public void addUsersData(String email, String authCode, Boolean albumCreated) {
        albumCreated = albumCreated != null && albumCreated;

        Map<String, Object> data = new HashMap<>();
        data.put(AUTH_CODE_TITLE, authCode);
        data.put(ALBUM_CREATED_TITLE, albumCreated);

        db.collection(USERS_COLLECTION)
            .document(email)
            .set(data)
            .addOnFailureListener(
                exception -> Log.d("users_collection", "Error adding document" + exception.getMessage())
            );
    }

    public Boolean getUserData(String email) {
        AtomicBoolean userDataCreated = new AtomicBoolean(false);
        db.collection(USERS_COLLECTION)
                .document(email)
                .get()
                .addOnSuccessListener(
                        documentSnapshot -> {
                            userDataCreated.set(true);
                        }
                )
                .addOnFailureListener(
                        exception -> Log.d("users_collection", "Error retrieving document" + exception.getMessage())
                );
        return userDataCreated.get();
    }

    public Boolean getAlbumCreated(String email) {
        if(this.getUserData(email)) {
            AtomicBoolean albumCreated = new AtomicBoolean(false);
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .get()
                    .addOnSuccessListener(
                            documentSnapshot -> {
                                albumCreated.set(Boolean.parseBoolean(String.valueOf(documentSnapshot.get(ALBUM_CREATED_TITLE))));
                            }
                    )
                    .addOnFailureListener(
                            exception -> Log.d("users_collection", "Error retrieving album created info" + exception.getMessage())
                    );
            return albumCreated.get();
        }
        return false;
    }

    public Boolean setAlbumCreated(String email) {
        AtomicBoolean canSetAlbumCreated = new AtomicBoolean(this.getAlbumCreated(email));
        if(canSetAlbumCreated.get()) {
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .update(ALBUM_CREATED_TITLE, true)
                    .addOnFailureListener(
                            exception -> {
                                canSetAlbumCreated.set(false);
                                Log.d("users_collection", "Error updating document" + exception.getMessage());
                            }
                    );
        }

        return canSetAlbumCreated.get();
    }

    public Boolean setAuthCode(String email, String authCode) {
        AtomicBoolean canSetAuthCode = new AtomicBoolean(this.getUserData(email));
        if(canSetAuthCode.get()) {
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .update(AUTH_CODE_TITLE, authCode)
                    .addOnFailureListener(
                            exception -> {
                                canSetAuthCode.set(false);
                                Log.d("users_collection", "Error updating document" + exception.getMessage());
                            }
                    );
        }

        return canSetAuthCode.get();
    }
}
