package fr.insset.ccm.m1.sag.travelogue.factory;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;

import java.io.IOException;

/**
 * A factory class that helps initialize a {@link PhotosLibraryClient} instance.
 */
public class PhotosLibraryClientFactory {
    private PhotosLibraryClientFactory() {
    }

    /**
     * Creates a new {@link PhotosLibraryClient} instance with credentials and scopes.
     */
    public static PhotosLibraryClient createClient(String token, String client_id, String client_secret)
            throws IOException {
        PhotosLibrarySettings settings =
                PhotosLibrarySettings.newBuilder()
                        .setCredentialsProvider(
                                FixedCredentialsProvider.create(
                                        getUserCredentials(token, client_id, client_secret)))
                        .build();
        return PhotosLibraryClient.initialize(settings);
    }

    private static Credentials getUserCredentials(String token, String client_id, String client_secret) {
        AccessToken a = new AccessToken(token, null);
        return UserCredentials.newBuilder().setClientId(client_id)
                .setClientSecret(client_secret).setAccessToken(a).build();
    }
}