package fr.insset.ccm.m1.sag.travelogue;

import android.Manifest;

public class Constants {
    public static final int LOCATION_SERVICE_ID = 175;
    public static final String ACTION_START_LOCATION_SERVICE = "startLocationService";
    public static final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";
    public static final int ALL_PERMISSION_CODE = 100;
    public static final int LOCATION_PERMISSION_CODE = 101;
    public static final String CAMERA_PERMISSION = android.Manifest.permission.CAMERA;
    public static final String ACCESS_COARSE_LOCATION_PERMISSION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String ACCESS_FINE_LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String ACCESS_BACKGROUND_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;

    public static final String FOREGROUND_SERVICE_PERMISSION = Manifest.permission.FOREGROUND_SERVICE;

}
