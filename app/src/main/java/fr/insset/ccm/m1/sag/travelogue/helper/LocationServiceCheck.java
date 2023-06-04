package fr.insset.ccm.m1.sag.travelogue.helper;

import android.content.Context;
import android.location.LocationManager;

public class LocationServiceCheck {

    private final Context context;

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
