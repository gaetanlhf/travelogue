package fr.insset.ccm.m1.sag.travelogue.helper.stockage;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedMethods;

public class ManageImages {

    /*
    match /{userId}/{documents=**} {
       allow write: if request.auth.uid == userId
       allow create: if request.auth.uid != null;
       allow read: if request.auth.uid == userId;
     }
     */

    // Get a non-default Storage bucket
    // FirebaseStorage storage = FirebaseStorage.getInstance("gs://my-custom-bucket");
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final StorageReference rootStorage = storage.getReference();
    private static final String referenceSeparator = "/";

    private static final String images_reference = "images";
    private static final String userEmailMetadataTitle = "ownerEmail";

    public static boolean initializeStorage(String userEmail) {
        if(userEmail != null && !userEmail.equals("")){
            // Create user reference
            StorageReference userRef = rootStorage.child(buildReferencePath(userEmail));
            return true;
        }

        return false;
    }

    public static boolean initializeTravelStorage(String userEmail, String travelId) {
        if(userEmail != null && !userEmail.equals("")){
            // Check if it already exists
             StorageReference travelRef = rootStorage.child(buildReferencePath(userEmail) + referenceSeparator + travelId);
            return true;
        }

        return false;
    }

    public static String addImageToTravelStorage(String userEmail, String travelId, File image, String imageName) {
        if(userEmail != null && !userEmail.equals("")){
            // Check if it already exists
            StorageReference imageRef = rootStorage.child(buildReferencePath(userEmail) + referenceSeparator + travelId + referenceSeparator + imageName);
            String imageRefPath = imageRef.getPath();

            if(image != null) {
                // Create file metadata including the content type
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType(Constants.IMAGES_CONTENT_TYPE)
                        .setCustomMetadata(userEmailMetadataTitle, userEmail)
                        .build();

                AtomicBoolean isSuccessFul = new AtomicBoolean(false);
                try {
                    byte[] data = FileUtils.readFileToByteArray(image);

                    // Upload the file and metadata
                    UploadTask uploadTask = imageRef.putBytes(data, metadata);
                    uploadTask.addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        displayManageImageError(Constants.UNABLE_TO_ADD_IMAGE_TO_REFERENCE + " => " + imageRefPath);
                    }).addOnSuccessListener(taskSnapshot -> {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        isSuccessFul.set(true);
                    });
                    if (isSuccessFul.get()) {
                        return imageRefPath;  // return the image reference path
                    }
                } catch (Exception e) {
                    displayManageImageError(e.getMessage());
                }
            }
        }

        return "";
    }

//    public static String getImageFromTravelStorage(String imageReferencePath) {
//        if(!imageReferencePath.equals("")){
//            StorageReference imageRef = rootStorage.child(imageReferencePath);
//            byte[] data = imageRef.getBytes();
//
//            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, byteArray.length);
//            ImageView image = (ImageView) findViewById(R.id.imageView1);
//            image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(), image.getHeight(), false));
//
//            return "";  // return the id/name of the image
//        }
//
//        return "";
//    }

    public static void deleteImage(String userEmail, String imageRefPath) {
        AtomicBoolean canDeleteImage = new AtomicBoolean(false);
        if(userEmail != null && !userEmail.equals("")){
            // Check if it already exists
            StorageReference desertRef = storage.getReference().child(imageRefPath);

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
        if(listRefPath != null && !listRefPath.equals("")){
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
        if(userEmail != null && !userEmail.equals("")){
             StorageReference travelRef = rootStorage.child(buildReferencePath(userEmail) + referenceSeparator + travelId);
            deleteImageInList(userEmail, travelRef.getPath());
            return true;  // return the id/name of the image
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
