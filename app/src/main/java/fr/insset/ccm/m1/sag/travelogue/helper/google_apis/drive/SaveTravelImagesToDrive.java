package fr.insset.ccm.m1.sag.travelogue.helper.google_apis.drive;

import android.accounts.Account;
import android.content.Context;
import android.content.res.Resources;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.helper.storage.ManageImages;

public class SaveTravelImagesToDrive {

    /**
     * Creates a Drive Service
     */
    private static Drive createDriveService(Context context, String userEmail) {
        Account account = new Account(userEmail, Constants.ACCOUNT_TYPE_FOR_DRIVE_SERVICE);

        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        context, Arrays.asList(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA));
        credential.setSelectedAccount(account);

        return new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName(Constants.APP_NAME)
                .build();
    }

    /**
     * Builds a travel folder name for the Drive
     */
    private static String buildTravelFolderName(String travelTitle, String travelId) {
        return travelTitle + "_" + travelId;
    }

    /**
     * Initializes the app (Travelogue) folder
     */
    public static String initializeTravelogueFolder(Resources resources, Context context, String userEmail) throws IOException {
        // Build a new authorized API client service.
        Drive service = createDriveService(context, userEmail);
        return createChildFolder(service, context, userEmail, Constants.APP_NAME, null);
    }

    /**
     * Create new folder.
     */
    public static String createChildFolder(Drive service, Context context, String userEmail, String folderName, String parentId) throws IOException {
        boolean folderExists = (searchCreatedFolder(service, folderName).size() > 0);
        if (!folderExists) {
            // Folder's metadata.
            File folderMetadata = new File();
            folderMetadata.setName(folderName);
            if (parentId != null) {
                folderMetadata.setParents(Collections.singletonList(parentId));
            }
            folderMetadata.setMimeType(Constants.DRIVE_FOLDER_MIME_TYPE);
            try {
                File folder = service.files().create(folderMetadata)
                        .setFields("id")
                        .execute();

                return folder.getId();
            } catch (GoogleJsonResponseException e) {
                System.err.println("Unable to create folder: " + e.getDetails());
                throw e;
            }
        } else {
            return searchCreatedFolder(service, folderName).get(0);
        }
    }

    /**
     * Create new folder without a parent folder.
     */
    private static String createFolder(Drive service, Context context, String userEmail) throws IOException {
        return createChildFolder(service, context, userEmail, Constants.APP_NAME, null);
    }

    public static void uploadFileBasic(Drive service, java.io.File imageFile, String fileName, String parentId) throws IOException {
        String filename = (fileName.contains(".jpeg") || fileName.contains(".jpg") || fileName.contains(".png")) ? fileName : fileName.concat(".jpeg");
        AtomicBoolean canUpload = new AtomicBoolean(true);

        List<String> createdFilesId = searchCreatedFile(service, parentId, filename);
        if (createdFilesId.size() == 0) {
            File fileMetadata = new File();
            fileMetadata.setName(filename);
            fileMetadata.setParents(Collections.singletonList(parentId));
            FileContent content = new FileContent(Constants.IMAGES_CONTENT_TYPE, imageFile);

            try {
                File fileLogo = service.files().create(fileMetadata, content)
                        .setFields("id, parents")
                        .execute();
                System.out.println(fileLogo.getId());
            } catch (IOException exc) {
                System.err.println("Unable to upload file: " + exc.getMessage());
                canUpload.set(false);
            }
        }

        canUpload.get();
    }

    public static boolean uploadFileFromInputStream(@Nullable Drive serviceN, InputStream inputStream, String fileName, String parentId, Context context, String userEmail) {
        Drive service = serviceN;
        if (serviceN == null) {
            service = createDriveService(context, userEmail);
        }

        String filename = (fileName.contains(".jpeg") || fileName.contains(".jpg") || fileName.contains(".png")) ? fileName : fileName.concat(".jpeg");
        AtomicBoolean isOk = new AtomicBoolean(false);

        File fileMetadata = new File();
        fileMetadata.setName(filename);
        fileMetadata.setParents(Collections.singletonList(parentId));
        InputStreamContent content = new InputStreamContent(Constants.IMAGES_CONTENT_TYPE, inputStream);

        try {
            File file = service.files().create(fileMetadata, content)
                    .setFields("id, parents")
                    .execute();
            System.out.println(file.getId());
            isOk.set(true);
        } catch (IOException exc) {
            System.err.println("Unable to upload file: " + exc.getMessage());
        }

        return isOk.get();
    }

    /**
     * Search for a specific folder
     */
    private static List<String> searchCreatedFolder(Drive service, String folderName) throws IOException {
        List<String> foldersId = new ArrayList<>();
        String pageToken = null;
        do {
            FileList result = service.files().list()
                    .setQ("mimeType = '" + Constants.DRIVE_FOLDER_MIME_TYPE + "' and name = '" + folderName + "' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();

            result.getFiles().forEach(f -> {
                foldersId.add(f.getId());  // Must return 0 or 1 element
            });

            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return foldersId;
    }

    /**
     * Search for a specific folder
     */
    private static List<String> searchCreatedFile(Drive service, String parentFolderId, String filename) throws IOException {
        List<String> filesId = new ArrayList<>();
        String pageToken = null;
        do {
            // '1234567' in parents
            FileList result = service.files().list()
                    .setQ("mimeType != '" + Constants.DRIVE_FOLDER_MIME_TYPE + "' and '" + parentFolderId + "' in parents and name = '" + filename + "' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();

            result.getFiles().forEach(f -> {
                filesId.add(f.getId());  // Must return 0 or 1 element
            });

            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return filesId;
    }

    public static String buildImageFileName(String travelDate, int imageNumber) {
        return ("JPEG_".concat(travelDate)).concat("_").concat(String.valueOf(imageNumber)).concat(".jpeg");
    }

    public static boolean exportTravelImagesToDrive(Context context, String userEmail, String travelogueFolderId, String travelId, String travelDate, String travelTitle) throws IOException {
        String travelFolderName = buildTravelFolderName(travelTitle, travelId);
        Drive service = createDriveService(context, userEmail);
        String travelFolderId = createChildFolder(service, context, userEmail, travelFolderName, travelogueFolderId);
        return ManageImages.downloadTravelImages(userEmail, travelId, travelDate, travelFolderId, service, context);
    }
}
