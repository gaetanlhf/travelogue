package fr.insset.ccm.m1.sag.travelogue.helper.google_apis.drive;

import android.content.res.Resources;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Arrays;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedMethods;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Users;

public class SaveTravelImagesToDrive {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private final Users users = new Users();

    private String accessToken;
    private String refreshToken;
    private Long expiresInSeconds;

    private boolean saveTravelImages(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
        this.currentUser = this.mAuth.getCurrentUser();

        // Get images from ManageImages (Firebase Storage)
        // boolean couldUploadAllFiles = true; List<String> errorImages ?
        // And loop it using uploadToFolder(travelogueDriveFolderId, file);
        // if error / null file, errorImages.add(file)
        // if(errorImages.size() > 0) couldUploadAllFiles = false;
        // return couldUploadAllFiles

        return false;
    }

    /**
     * Upload a file to the specified folder.
     *
     * @param realFolderId Id of the folder.
     * @return Inserted file metadata if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    public static File uploadToFolder(String realFolderId, java.io.File imageFile) throws IOException {
        // Load pre-authorized user credentials from the environment.
        // TODO(developer) - See https://developers.google.com/identity for
        // guides on implementing OAuth2 for your application.
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singletonList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(Constants.APP_NAME)
                .build();

        // File's metadata.
        File fileMetadata = new File();
        fileMetadata.setName(imageFile.getName());
        fileMetadata.setParents(Collections.singletonList(realFolderId));
        FileContent mediaContent = new FileContent(Constants.IMAGES_CONTENT_TYPE, imageFile);
        try {
            File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id, parents")
                    .execute();
            System.out.println("File ID: " + file.getId());
            return file;
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to upload file: " + e.getDetails());
            throw e;
        }
    }


    /**
     * Create new folder.
     *
     * @return Inserted folder id if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    public static String createFolder() throws IOException {
        // Load pre-authorized user credentials from the environment.
        // TODO(developer) - See https://developers.google.com/identity for
        // guides on implementing OAuth2 for your application.
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singletonList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(Constants.APP_NAME)
                .build();
        // File's metadata.
        File fileMetadata = new File();
        fileMetadata.setName(Constants.APP_NAME);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        try {
            File file = service.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            System.out.println("Folder ID: " + file.getId());
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to create folder: " + e.getDetails());
            throw e;
        }
    }



    private Thread startGoogleDriveClient(String userAuthCode, Resources resources) {
        if (currentUser != null) {
            if (!users.getAlbumCreated(currentUser.getEmail())) {
                if(!userAuthCode.equals("")) {
                    return new Thread(() -> {
                        // do background stuff here
                        String REDIRECT_URI = "";
                        GoogleClientSecrets clientSecrets = null;
                        try {
                            clientSecrets = SharedMethods.getClientSecrets(resources);
                            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                                    new NetHttpTransport(),
                                    GsonFactory.getDefaultInstance(),
                                    "https://www.googleapis.com/oauth2/v4/token",
                                    clientSecrets.getDetails().getClientId(),
                                    clientSecrets.getDetails().getClientSecret(),
                                    userAuthCode,
                                    REDIRECT_URI)
                                    .execute();
                            accessToken = tokenResponse.getAccessToken();
                            refreshToken = tokenResponse.getRefreshToken();
                            expiresInSeconds = tokenResponse.getExpiresInSeconds();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

//                        runOnUiThread(() -> {
//                            // OnPostExecute stuff here
//                        });
                    });
                }
            }
        }

        return null;
    }
}
