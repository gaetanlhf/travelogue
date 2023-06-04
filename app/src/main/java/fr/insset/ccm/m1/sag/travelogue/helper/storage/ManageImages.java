package fr.insset.ccm.m1.sag.travelogue.helper.storage;

import android.content.Context;

import com.google.api.services.drive.Drive;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedMethods;
import fr.insset.ccm.m1.sag.travelogue.helper.google_apis.drive.SaveTravelImagesToDrive;

public class ManageImages {
    // Get a non-default Storage bucket
    // FirebaseStorage storage = FirebaseStorage.getInstance("gs://my-custom-bucket");
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final StorageReference rootStorage = storage.getReference();
    private static final String referenceSeparator = "/";

    private static final String images_reference = "images";
    private static final String userEmailMetadataTitle = "ownerEmail";

    public static boolean initializeStorage(String userEmail) {
        if (userEmail != null && !userEmail.equals("")) {
            // Create user reference
            StorageReference userRef = rootStorage.child(buildReferencePath(userEmail));
            return true;
        }

        return false;
    }

    public static boolean initializeTravelStorage(String userEmail, String travelId) {
        if (userEmail != null && !userEmail.equals("")) {
            // Check if it already exists
            StorageReference travelRef = rootStorage.child(buildReferencePath(userEmail) + referenceSeparator + travelId);
            return true;
        }

        return false;
    }

    public static String addImageToTravelStorage(String userEmail, String travelId, File image) {
        if (userEmail != null && !userEmail.equals("")) {
            // Check if it already exists
            StorageReference imageRef = rootStorage.child(buildReferencePath(userEmail) + referenceSeparator + travelId + referenceSeparator + image.getName());
            String imageRefPath = buildReferencePath(userEmail) + referenceSeparator + travelId + referenceSeparator + image.getName();

            // Create file metadata including the content type
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType(Constants.IMAGES_CONTENT_TYPE)
                    .setCustomMetadata(userEmailMetadataTitle, userEmail)
                    .build();

            try {
                byte[] data = FileUtils.readFileToByteArray(image);

                // Upload the file and metadata
                UploadTask uploadTask = imageRef.putBytes(data, metadata);
                uploadTask.addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    displayManageImageError(Constants.UNABLE_TO_ADD_IMAGE_TO_REFERENCE + " => " + imageRefPath);
                });
                return imageRefPath;
            } catch (Exception e) {
                displayManageImageError(e.getMessage());
            }
        }

        return "";
    }

    public static void deleteImage(String userEmail, String imageRefPath) {
        AtomicBoolean canDeleteImage = new AtomicBoolean(false);
        if (userEmail != null && !userEmail.equals("") && imageRefPath != null && !imageRefPath.equals("")) {
            // Check if it already exists
            StorageReference desertRef = rootStorage.child(imageRefPath);

            // Delete the file
            desertRef.delete().addOnSuccessListener(aVoid -> {
                // File deleted successfully
                canDeleteImage.set(true);
            }).addOnFailureListener(exception -> {
                // Uh-oh, an error occurred!
                displayManageImageError(Constants.UNABLE_TO_DELETE_IMAGE_FROM_REFERENCE + " => " + imageRefPath);
            });
        }

        canDeleteImage.get();
    }

    private static void deleteImageInList(String userEmail, String listRefPath) {
        if (listRefPath != null && !listRefPath.equals("")) {
            StorageReference listRef = storage.getReference().child(listRefPath);
            listRef.listAll()
                    .addOnSuccessListener(listResult -> {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                            deleteImageInList(userEmail, prefix.getPath());
                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            deleteImage(userEmail, item.getPath());
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Uh-oh, an error occurred!
                        displayManageImageError(Constants.UNABLE_TO_DELETE_TRAVEL_REFERENCE + " => " + listRefPath);
                    });
        }
    }

    public static void deleteTravelStorage(String userEmail, String travelId) {
        if (userEmail != null && !userEmail.equals("")) {
            StorageReference travelRef = rootStorage.child(buildReferencePath(userEmail) + referenceSeparator + travelId);
            deleteImageInList(userEmail, travelRef.getPath());
        }

    }

    public static boolean downloadTravelImages(String userEmail, String travelId, String travelDate, String travelFolderId, Drive service, Context context) {
        if (userEmail != null && !userEmail.equals("")) {
            StorageReference travelRef = rootStorage.child(buildReferencePath(userEmail) + referenceSeparator + travelId);
            AtomicInteger imageNb = new AtomicInteger(1);
            AtomicBoolean test = new AtomicBoolean();
            travelRef.listAll()
                    .addOnSuccessListener(listResult -> {
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            String filename = SaveTravelImagesToDrive.buildImageFileName(travelDate, imageNb.get());
                            java.io.File localFile = new java.io.File(context.getCacheDir(), filename);
                            item.getFile(localFile.getAbsoluteFile()).addOnSuccessListener(taskSnapshot -> {
                                // Local temp file has been created
                                new Thread(() -> {
                                    try {
                                        SaveTravelImagesToDrive.uploadImageFile(service, localFile, filename, travelFolderId);
                                    } catch (IOException exception) {
                                        SharedMethods.displayDebugLogMessage("test_download", "Cannot download image");
                                    }
                                }).start();
                            }).addOnFailureListener(exception -> {
                                // Handle any errors
                                SharedMethods.displayDebugLogMessage("test_download", "Cannot download image");
                            });
                            imageNb.getAndIncrement();
                        }
                        test.set(true);
                    })
                    .addOnFailureListener(e -> {
                        // Uh-oh, an error occurred!
                        displayManageImageError(Constants.UNABLE_TO_COUNT_IMAGES_IN_TRAVEL_REFERENCE + " => " + travelId);
                    });
            return test.get();
        }
        return false;
    }

    private static String buildReferencePath(String userEmail) {
        return images_reference + referenceSeparator + userEmail;
    }

    public static void displayManageImageError(String message) {
        SharedMethods.displayDebugLogMessage(Constants.IMAGES_MANAGEMENT_LOG_TAG, message);
    }
}
