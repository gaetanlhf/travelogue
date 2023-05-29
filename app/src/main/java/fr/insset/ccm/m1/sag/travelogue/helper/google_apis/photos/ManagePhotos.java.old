package fr.insset.ccm.m1.sag.travelogue.helper.google_apis.photos;

import android.content.Context;
import android.content.res.Resources;
import android.os.Trace;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.BatchCreateMediaItemsResponse;
import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.library.v1.proto.NewMediaItemResult;
import com.google.photos.library.v1.upload.UploadMediaItemRequest;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.library.v1.util.NewMediaItemFactory;
import com.google.photos.types.proto.Album;
import com.google.photos.types.proto.MediaItem;
import com.google.rpc.Code;
import com.google.rpc.Status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.factory.PhotosLibraryClientFactory;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedMethods;

public class ManagePhotos {
    private static final String unableToCreateAlbum = "Unable to create album";

    public static String createAlbum(String albumTitle, String accessToken, Resources resources, Context context) {
        String createdAlbumId = "";
        if(!albumTitle.equals("")) {
            try {
                // Create album
                GoogleClientSecrets clientSecrets = SharedMethods.getClientSecrets(resources);
                try (PhotosLibraryClient photosLibraryClient = PhotosLibraryClientFactory.createClient(
                        accessToken,
                        clientSecrets.getDetails().getClientId(),
                        clientSecrets.getDetails().getClientSecret())
                ) {
                    try {

                        // Create a new Album  with at title
                        Album createdAlbum = photosLibraryClient.createAlbum("Album de lolita 2");

                        // Get some properties from the album, such as its ID and product URL
                        String id = createdAlbum.getId();
                        String url = createdAlbum.getProductUrl();

                        createdAlbumId = id;

                        // When everything is successful
                        SharedMethods.displayToast(context, "Successful creation");
                    } catch (Exception e) {
                        ManagePhotos.displayAlbumCreationError(context, albumTitle);
                    }
                } catch (Exception e) {
                    ManagePhotos.displayAlbumCreationError(context, albumTitle);
                }
            } catch (IOException ioException) {
                Log.d(Constants.ALBUM_CREATION_LOG_TAG, ioException.getMessage());
                ManagePhotos.displayAlbumCreationError(context, albumTitle);
            }
        }

        return createdAlbumId;
    }

    private static void displayAlbumCreationError(Context context, String albumTitle) {
        Log.d(Constants.ALBUM_CREATION_LOG_TAG, ManagePhotos.unableToCreateAlbum + ": " + albumTitle);
        // TODO Delete the following line
        SharedMethods.displayToast(context, ManagePhotos.unableToCreateAlbum + ": " + albumTitle);
    }

    // TODO Upload photo and read photo

    private void uploadPhoto(String albumId, String accessToken, Resources resources, File imageFile, String fileMimeType) {
        try {
            GoogleClientSecrets clientSecrets = SharedMethods.getClientSecrets(resources);
            try (PhotosLibraryClient photosLibraryClient = PhotosLibraryClientFactory.createClient(
                    accessToken,
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret())
            ) {
                try (RandomAccessFile file = new RandomAccessFile(imageFile, "r")) {
                    // Create a new upload request
                    UploadMediaItemRequest uploadRequest =
                            UploadMediaItemRequest.newBuilder()
                                    // The media type (e.g. "image/jpeg")
                                    .setMimeType(fileMimeType)
                                    // The file to upload
                                    .setDataFile(file)
                                    .build();
                    // Upload and capture the response
                    UploadMediaItemResponse uploadResponse = photosLibraryClient.uploadMediaItem(uploadRequest);
                    if (uploadResponse.getError().isPresent()) {
                        // If the upload results in an error, handle it
                        UploadMediaItemResponse.Error error = uploadResponse.getError().get();
//                Toast.makeText(this, error.getCause().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        // If the upload is successful, get the uploadToken
                        String uploadToken = uploadResponse.getUploadToken().get();
                        // Use this upload token to create a media item

                        String fileName = imageFile.getName();
                        String itemDescription = "Trip: " + imageFile.getName();

                        // Create a NewMediaItem with the following components:
                        // - uploadToken obtained from the previous upload request
                        // - filename that will be shown to the user in Google Photos
                        // - description that will be shown to the user in Google Photos
                        NewMediaItem newMediaItem = NewMediaItemFactory
                                .createNewMediaItem(uploadToken, fileName, itemDescription);
                        List<NewMediaItem> newItems = Collections.singletonList(newMediaItem);

                        BatchCreateMediaItemsResponse response = photosLibraryClient.batchCreateMediaItems(newItems);
                        for (NewMediaItemResult itemsResponse : response.getNewMediaItemResultsList()) {
                            Status status = itemsResponse.getStatus();
                            if (status.getCode() == Code.OK_VALUE) {
                                // The item is successfully created in the user's library
                                MediaItem createdItem = itemsResponse.getMediaItem();
                            } else {
                                // The item could not be created. Check the status and try again
//                            Toast.makeText(this, "Can't create media item", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (IOException e) {
                    // Handle error
                    Log.d("media_item_creation", "API exception: " + e.getMessage());
                }
            } catch (IOException e) {
                // Handle error
                Log.d("media_item_creation", "API exception: " + e.getMessage());
            }
        } catch (IOException e) {
            // Handle error
            Log.d("media_item_creation", "API exception: " + e.getMessage());
        }
    }

    private void addImage(String albumId, String albumTitle, File image) {

        // Add media items
//        try {
//            // List of media item IDs to add
//            List<String> mediaItemIds = Arrays
//                    .asList("MEDIA_ITEM_ID", "ANOTHER_MEDIA_ITEM_ID");
//
//            // ID of the album to add media items to
//            String albumId = "ALBUM_ID";
//
//            // Add all given media items to the album
//            photosLibraryClient.batchAddMediaItemsToAlbum(albumId, mediaItemIds);
//
//        } catch (ApiException e) {
//            // An exception is thrown if the media items could not be added
//        }

        // return mediaItemId; ??
    }

    public void deleteImage(String albumId, String albumTitle, File image) {

        // Remove all media items
//        try {
//            // List of media item IDs to remove
//            List<String> mediaItemIds = Arrays
//                    .asList("MEDIA_ITEM_ID", "ANOTHER_MEDIA_ITEM_ID");
//
//            // ID of the album to remove media items from
//            String albumId = "ALBUM_ID";
//
//            // Remove all given media items from the album
//            photosLibraryClient.batchRemoveMediaItemsFromAlbum(albumId, mediaItemIds);
//
//        } catch (ApiException e) {
//            // An exception is thrown if the media items could not be removed
//        }


        // return true if deleted;
    }
}
