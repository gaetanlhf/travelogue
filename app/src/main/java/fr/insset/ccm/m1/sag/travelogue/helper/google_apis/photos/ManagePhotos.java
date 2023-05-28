package fr.insset.ccm.m1.sag.travelogue.helper.google_apis.photos;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.types.proto.Album;
import com.google.photos.types.proto.MediaItem;

import java.io.File;
import java.io.IOException;

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

    private void uploadPhoto(String uploadToken, String albumId, String albumTitle) {

    }

    public void addImage(String albumId, String albumTitle, File image) {

        // return mediaItemId; ??
    }

    public void deleteImage(String albumId, String albumTitle, File image) {

        // return true if deleted;
    }
}
