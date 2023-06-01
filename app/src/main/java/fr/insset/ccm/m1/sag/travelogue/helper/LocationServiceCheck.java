package fr.insset.ccm.m1.sag.travelogue.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;

import android.content.Context;
import android.location.LocationManager;
import android.content.Intent;
import android.provider.Settings;

public class LocationServiceCheck {

    private Context context;

    public LocationServiceCheck(Context context) {
        this.context = context;
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = null;
        if (context != null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
