package fr.insset.ccm.m1.sag.travelogue.helper.storage;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedMethods;

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

    public static boolean deleteTravelStorage(String userEmail, String travelId) {
        if (userEmail != null && !userEmail.equals("")) {
            StorageReference travelRef = rootStorage.child(buildReferencePath(userEmail) + referenceSeparator + travelId);
            deleteImageInList(userEmail, travelRef.getPath());
            return true;  // return the id/name of the image
        }

        return false;
    }

    public static int countImagesInTravel(String userEmail, String travelId) {
        AtomicReference<Integer> count = new AtomicReference<>(0);
        if (userEmail != null && !userEmail.equals("")) {
            StorageReference travelRef = rootStorage.child(buildReferencePath(userEmail) + referenceSeparator + travelId);
            travelRef.listAll()
                    .addOnSuccessListener(listResult -> {
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            count.set(count.get() + 1);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Uh-oh, an error occurred!
                        displayManageImageError(Constants.UNABLE_TO_COUNT_IMAGES_IN_TRAVEL_REFERENCE + " => " + travelId);
                    });
        }

        return count.get();
    }

    public static List<InputStream> getImagesInTravel(String userEmail, String travelId) {
        List<InputStream> travelImages = new ArrayList<>();
        if (userEmail != null && !userEmail.equals("")) {
            StorageReference travelRef = rootStorage.child(buildReferencePath(userEmail) + referenceSeparator + travelId);
            travelRef.listAll()
                    .addOnSuccessListener(listResult -> {
                        for (StorageReference item : listResult.getItems()) {
                            // String imageRefPath = buildReferencePath(userEmail) + referenceSeparator + travelId + referenceSeparator + image.getName();
                            // All the items under listRef.

                            item.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                                // Use the bytes to display the image
                                InputStream inputStream = new ByteArrayInputStream(bytes);
                                travelImages.add(inputStream);
                            }).addOnFailureListener(exception -> {
                                // Handle any errors
                                SharedMethods.displayDebugLogMessage("Getting_images_bytes", exception.getMessage());
                            });

//                            InputStream tt =
                            /*
                            storageRef.child("users/me/profile.png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Use the bytes to display the image
                                    InputStream myInputStream = new ByteArrayInputStream(myBytes);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

                            StorageReference islandRef = storageRef.child("images/island.jpg");
                            final long ONE_MEGABYTE = 1024 * 1024;
                            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Data for "images/island.jpg" is returns, use this as needed
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                             */

                            /*
                            islandRef = storageRef.child("images/island.jpg");
                            File localFile = File.createTempFile("images", "jpg");

                            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // Local temp file has been created
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                             */

                        }
                    })
                    .addOnFailureListener(e -> {
                        // Uh-oh, an error occurred!
                        displayManageImageError(Constants.UNABLE_TO_COUNT_IMAGES_IN_TRAVEL_REFERENCE + " => " + travelId);
                    });
        }

        return travelImages;
    }

    private static String buildReferencePath(String userEmail) {
        return images_reference + referenceSeparator + userEmail;
    }

    public static void displayManageImageError(String message) {
        SharedMethods.displayDebugLogMessage(Constants.IMAGES_MANAGEMENT_LOG_TAG, message);
    }
}
