package fr.insset.ccm.m1.sag.travelogue.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;

public class SharedMethods {
    public static void displayToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void displayLogMessage(int priority, String tag, String message) {
        Log.println(priority, tag, message);
    }

    public static void displayDebugLogMessage(String tag, String message) {
        Log.d(tag, message);
    }

    public static GoogleClientSecrets getClientSecrets(Resources resources) throws IOException {
        InputStream inputStream = resources.openRawResource(R.raw.server_client_secret);
        return GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(), new InputStreamReader(inputStream));
    }
}
