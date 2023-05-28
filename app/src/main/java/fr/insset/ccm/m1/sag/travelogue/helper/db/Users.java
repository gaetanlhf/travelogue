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
    private final static String AUTH_CODE_TITLE = "authCode";
    private final static String ALBUM_CREATED_TITLE = "albumCreated";
    private final static String TRAVELOGUE_ALBUM_ID = "travelogueAlbumId";
    private final static String ACCESS_TOKEN_TITLE = "accessToken";
    private final static String REFRESH_TOKEN_TITLE = "refreshToken";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Users() {
    }

    public void addUsersData(String email, String authCode, Boolean albumCreated) {
        albumCreated = albumCreated != null && albumCreated;

        Map<String, Object> data = new HashMap<>();
        data.put(AUTH_CODE_TITLE, authCode);
        data.put(ACCESS_TOKEN_TITLE, "");
        data.put(REFRESH_TOKEN_TITLE, "");
        data.put(ALBUM_CREATED_TITLE, albumCreated);
        data.put(TRAVELOGUE_ALBUM_ID, "");

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
        if (this.getUserData(email)) {
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

    public String getAuthCode(String email) {
        if (this.getUserData(email)) {
            AtomicReference<String> authCode = new AtomicReference<>(new String(""));
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .get()
                    .addOnSuccessListener(
                            documentSnapshot -> {
                                authCode.set(String.valueOf(documentSnapshot.get(AUTH_CODE_TITLE)));
                            }
                    )
                    .addOnFailureListener(
                            exception -> Log.d("users_collection", "Error retrieving album created info" + exception.getMessage())
                    );
            return authCode.get();
        }
        return "";
    }

    public Boolean setAuthCode(String email, String authCode) {
        AtomicBoolean canSetAuthCode = new AtomicBoolean(this.getUserData(email));
        if (canSetAuthCode.get()) {
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

    public List<String> getTokens(String email) {
        AtomicBoolean canGetTokens = new AtomicBoolean(this.getUserData(email));
        List<String> tokens = new ArrayList<>();
        if (canGetTokens.get()) {
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .get()
                    .addOnSuccessListener(
                            documentSnapshot -> {
                                tokens.add(String.valueOf(documentSnapshot.get(ACCESS_TOKEN_TITLE)));
                                tokens.add(String.valueOf(documentSnapshot.get(REFRESH_TOKEN_TITLE)));
                            }
                    )
                    .addOnFailureListener(
                            exception -> {
                                canGetTokens.set(false);
                                Log.d("users_collection", "Error updating document" + exception.getMessage());
                            }
                    );
        }

        return tokens;
    }

    public Boolean setTokens(String email, String accessToken, String refreshToken) {
        AtomicBoolean canSetTokens = new AtomicBoolean(this.getUserData(email));
        if (canSetTokens.get()) {
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .update(
                        ACCESS_TOKEN_TITLE, accessToken,
                        REFRESH_TOKEN_TITLE, refreshToken
                    )
                    .addOnFailureListener(
                            exception -> {
                                canSetTokens.set(false);
                                Log.d("users_collection", "Error updating document" + exception.getMessage());
                            }
                    );
        }

        return canSetTokens.get();
    }

    public String getTravelogueAlbumId(String email) {
        AtomicBoolean canGetTravelogueAlbumId = new AtomicBoolean(this.getUserData(email));
        AtomicReference<String> travelogueAlbumId = new AtomicReference<>("");
        if (canGetTravelogueAlbumId.get()) {
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .get()
                    .addOnSuccessListener(
                            documentSnapshot -> {
                                travelogueAlbumId.set(String.valueOf(documentSnapshot.get(TRAVELOGUE_ALBUM_ID)));
                            }
                    )
                    .addOnFailureListener(
                            exception -> {
                                canGetTravelogueAlbumId.set(false);
                                Log.d("users_collection", "Error updating document" + exception.getMessage());
                            }
                    );
        }

        return travelogueAlbumId.get();
    }

    public Boolean setTravelogueAlbumId(String email, String albumId) {
        AtomicBoolean canSetTravelogueAlbumId = new AtomicBoolean(this.getUserData(email));
        AtomicBoolean travelogueAlbumIdAlreadySet = new AtomicBoolean(!this.getTravelogueAlbumId(email).equals(""));
        if (canSetTravelogueAlbumId.get() && !travelogueAlbumIdAlreadySet.get()) {
            db.collection(USERS_COLLECTION)
                    .document(email)
                    .update(
                            TRAVELOGUE_ALBUM_ID, albumId,
                            ALBUM_CREATED_TITLE, true
                    )
                    .addOnFailureListener(
                            exception -> {
                                canSetTravelogueAlbumId.set(false);
                                Log.d("users_collection", "Error updating document" + exception.getMessage());
                            }
                    );
        } else {
            Log.d("users_collection", "Must be already set or can't access document!");
        }

        return canSetTravelogueAlbumId.get();
    }
}
