package fr.insset.ccm.m1.sag.travelogue.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

import fr.insset.ccm.m1.sag.travelogue.R;

public class PermissionHelper {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 101;

    private static final String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
    };

    private static final String[] backgroundLocationPermission = {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    public static void verifyPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!areAllPermissionsGranted(activity, permissions)) {
                requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
            } else if (!areAllPermissionsGranted(activity, backgroundLocationPermission)) {
                if (shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    showAlertAndOpenAppSettings(activity);
                } else {
                    requestPermissions(activity, backgroundLocationPermission, BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!areAllPermissionsGranted(activity, concatAll(permissions, backgroundLocationPermission))) {
                requestPermissions(activity, concatAll(permissions, backgroundLocationPermission), PERMISSION_REQUEST_CODE);
            }
        } else {
            if (!areAllPermissionsGranted(activity, permissions)) {
                requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
            }
        }
    }


    public static void showAlertAndOpenAppSettings(Activity activity) {
        new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.background_location_permission_alert_title)
                .setMessage(R.string.background_location_permission_alert_desc)
                .setPositiveButton(R.string.background_location_permission_alert_open_settings, (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivity(intent);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    activity.finish();
                })
                .create()
                .show();
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    private static void requestPermissions(Activity activity, String[] permissionsToRequest, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissionsToRequest, requestCode);
    }

    public static boolean arePermissionsGranted(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE || requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean areAllPermissionsGranted(Activity activity, String[] permissionsToCheck) {
        for (String permission : permissionsToCheck) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean areAllBasicPermissionsGranted(Activity activity) {
        return areAllPermissionsGranted(activity, permissions);
    }

    public static boolean isBackgroundLocationPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return areAllPermissionsGranted(activity, backgroundLocationPermission);
        }
        return true;
    }

    private static String[] concatAll(String[] first, String[]... rest) {
        int totalLength = first.length;
        for (String[] array : rest) {
            totalLength += array.length;
        }
        String[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (String[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
