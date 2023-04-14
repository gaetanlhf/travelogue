package fr.insset.ccm.m1.sag.travelogue.services;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;

public class LocationService extends Service {

    public static boolean isServiceRunning = false;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            locationResult.getLastLocation();
            double latitude = locationResult.getLastLocation().getLatitude();
            double longitude = locationResult.getLastLocation().getLongitude();
            Log.d("LOCATION_UPDATE", latitude + " , " + longitude);

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService(){
        Log.d("Service", "Launch");
        isServiceRunning = true;

        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if(notificationManager != null && notificationManager.getNotificationChannel(channelId) == null){
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    "Location Service",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setDescription("This channel is used by location service");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());

    }

    private void stopLocationService(){
        Log.d("test", "ok stop loc service");
        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent != null){
            String action = intent.getAction();
            if(action != null){
                Log.d("IDACTION", action);
                if(action.equals(Constants.ACTION_START_LOCATION_SERVICE)){
                    startLocationService();
                }else if(action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)){
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
