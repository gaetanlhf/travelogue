package fr.insset.ccm.m1.sag.travelogue.helper.db;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Users {

    private final static String USERS_COLLECTION = "users";
    private final static String ALBUM_CREATED_TITLE = "albumCreated";
    private final static String DRIVE_FOLDER_ID = "driveFolderId";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Users() {
    }

    public void addUsersData(String email, Boolean albumCreated) {
        albumCreated = albumCreated != null && albumCreated;

        Map<String, Object> data = new HashMap<>();
        data.put(ALBUM_CREATED_TITLE, albumCreated);
        data.put(DRIVE_FOLDER_ID, "");

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
        AtomicBoolean canGetAlbumCreated = new AtomicBoolean(this.getUserData(email));
        if (canGetAlbumCreated.get()) {
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .get()
                    .addOnSuccessListener(
                            documentSnapshot -> {
                                canGetAlbumCreated.set(Boolean.parseBoolean(String.valueOf(documentSnapshot.get(ALBUM_CREATED_TITLE))));
                            }
                    )
                    .addOnFailureListener(
                            exception -> {
                                canGetAlbumCreated.set(false);
                                Log.d("users_collection", "Error retrieving album created info" + exception.getMessage());
                            }
                    );
        }
        return canGetAlbumCreated.get();
    }

    public Boolean setAlbumCreated(String email) {
        AtomicBoolean canSetAlbumCreated = new AtomicBoolean(getAlbumCreated(email));
        if (!canSetAlbumCreated.get()) {
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .update(ALBUM_CREATED_TITLE, true)
                    .addOnSuccessListener(
                            documentSnapshot -> {
                                canSetAlbumCreated.set(true);
                            }
                    )
                    .addOnFailureListener(
                            exception -> {
                                canSetAlbumCreated.set(false);
                                Log.d("users_collection", "Error updating document" + exception.getMessage());
                            }
                    );
        }
        return canSetAlbumCreated.get();
    }

    public String getDriveFolderId(String email) {
        boolean canGetUserData = this.getUserData(email);
        AtomicReference<String> driveFolderId = new AtomicReference<String>("");
        if (canGetUserData) {
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .get()
                    .addOnSuccessListener(
                            documentSnapshot -> {
                                driveFolderId.set(String.valueOf(documentSnapshot.get(DRIVE_FOLDER_ID)));
                            }
                    )
                    .addOnFailureListener(
                            exception -> {
                                Log.d("users_collection", "Error retrieving album created info" + exception.getMessage());
                            }
                    );
        }
        return driveFolderId.get();
    }

    public Boolean setDriveFolderId(String email, String driveFolderId) {
        String userDriveFolderId = getDriveFolderId(email);
        AtomicBoolean canSetDriveFolderID = new AtomicBoolean(false);
        if (userDriveFolderId != null && !userDriveFolderId.equals("")) {
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .update(DRIVE_FOLDER_ID, driveFolderId)
                    .addOnSuccessListener(
                            documentSnapshot -> {
                                canSetDriveFolderID.set(true);
                            }
                    )
                    .addOnFailureListener(
                            exception -> {
                                Log.d("users_collection", "Error updating document" + exception.getMessage());
                            }
                    );
        }
        return canSetDriveFolderID.get();
    }
}
