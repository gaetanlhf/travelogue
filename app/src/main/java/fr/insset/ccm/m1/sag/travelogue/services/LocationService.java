package fr.insset.ccm.m1.sag.travelogue.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedPrefManager;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Location;

public class LocationService extends Service {

    public static boolean isServiceRunning = false;
    private final GpsPoint gpsPoint = new GpsPoint(0, 0, "0");
    private FirebaseAuth mAuth;
    private SharedPrefManager sharedPrefManager;

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                super.onLocationResult(locationResult);
                locationResult.getLastLocation();
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                gpsPoint.setLongitude(longitude);
                gpsPoint.setLatitude(latitude);
                gpsPoint.setLinkedDataType("none");
                gpsPoint.setLinkedData("none");
                Location location = new Location(currentUser.getUid());
                sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                location.addPoint(gpsPoint, sharedPrefManager.getString("CurrentTravel"));
                Intent intent = new Intent("updateHomeFragment");
                sendBroadcast(intent);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    private void startLocationService(Long timeBetweenUpdateLocation) {
        mAuth = FirebaseAuth.getInstance();

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
        builder.setContentTitle(getString(R.string.travelogue_service_name));
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText(getString(R.string.travelogue_service_running));
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    getString(R.string.travelogue_service_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setDescription("This channel is used by location service");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        LocationRequest locationRequest = new LocationRequest.Builder(timeBetweenUpdateLocation * 60000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(timeBetweenUpdateLocation * 1000)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .build();


        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
        isServiceRunning = true;

    }

    public void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService(intent.getLongExtra("timeBetweenUpdate", 5));
                } else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /* A implémenter dans l'activité qui utilise le service
    private void startLocationService(){
        if(!LocationService.isServiceRunning){
            if(!PermissionsHelper.hasPermission(this, Constants.ACCESS_FINE_LOCATION_PERMISSION)){
                PermissionsHelper.requestPermissions(this, new String[] {Constants.ACCESS_BACKGROUND_LOCATION_PERMISSION, Constants.ACCESS_COARSE_LOCATION_PERMISSION, Constants.ACCESS_FINE_LOCATION_PERMISSION, Constants.FOREGROUND_SERVICE_PERMISSION}, Constants.LOCATION_PERMISSION_CODE);
            }
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(LocationService.isServiceRunning){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent); //remplacé par startService car stopService ne fonctionne pas (wtf)
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsHelper.hasPermissions(this, permissions)) {
            startLocationService();
            Log.d("Permission", "Accordé");
        } else {
            Log.d("Permission", "Refusé");
        }
    }*/

}
