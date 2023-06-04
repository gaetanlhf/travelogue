package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedMethods;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedPrefManager;
import fr.insset.ccm.m1.sag.travelogue.helper.db.InitDatabase;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Settings;
import fr.insset.ccm.m1.sag.travelogue.helper.db.State;
import fr.insset.ccm.m1.sag.travelogue.helper.google_apis.drive.SaveTravelImagesToDrive;
import fr.insset.ccm.m1.sag.travelogue.helper.storage.ManageImages;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPrefManager sharedPrefManager;
    private Thread connectivityCheckThread;
    private volatile boolean threadRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        setContentView(R.layout.activity_main);
        threadRunning = true;
        connectivityCheckThread = new Thread(() -> {
            while (threadRunning) {
                if (!NetworkConnectivityCheck.isNetworkAvailableAndConnected(this)) {
                    Intent intent = new Intent(this, NoConnection.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                    break;
                }

                try {
                    Thread.sleep(Constants.TIME_CHECK_CONNECTION); // Check every 2 seconds
                } catch (InterruptedException e) {
                    threadRunning = false;
                }
            }
        });
        connectivityCheckThread.start();
        sharedPrefManager = SharedPrefManager.getInstance(this);
        sharedPrefManager.saveBool("PermissionsRequested", false);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            InitDatabase initDatabase = new InitDatabase(currentUser.getUid());
            initDatabase.isInit(init -> {
                if (!init.get()) {
                    initDatabase.initDb();
                    boolean ok = ManageImages.initializeStorage(currentUser.getEmail());
                    if (!ok) {
                        SharedMethods.displayDebugLogMessage(Constants.IMAGES_MANAGEMENT_LOG_TAG, Constants.UNABLE_TO_INITIALIZE_ROOT_STORAGE);
                    }
                }

                Settings settings = new Settings(currentUser.getUid());
                settings.getSettings(atomicReferenceArray -> {
                    sharedPrefManager.saveLong("TimeBetweenAutoGps", Long.parseLong(atomicReferenceArray.get(1).toString()));
                    sharedPrefManager.saveBool("AutoGps", Boolean.parseBoolean(atomicReferenceArray.get(0).toString()));
                });

                State state = new State(currentUser.getUid());

                state.getTravelogueFolderId(travelogueFolderId1 -> {
                    if (travelogueFolderId1.get() == null || travelogueFolderId1.get().equals("")) {
                        new Thread(() -> {
                            try {
                                String travelogueFolderId = SaveTravelImagesToDrive.initializeTravelogueFolder(getResources(), this, currentUser.getEmail());
                                state.setTravelogueFolderId(this, travelogueFolderId);
                                sharedPrefManager.saveString(Constants.DRIVE_FOLDER_DATABASE_KEY, travelogueFolderId);

                                if (travelogueFolderId != null) {
                                    SaveTravelImagesToDrive.uploadFileFromInputStream(
                                            null, getResources().openRawResource(R.raw.travelogue_logo), "travelogue_logo.jpeg",
                                            travelogueFolderId, getApplicationContext(), currentUser.getEmail()
                                    );
                                }
                            } catch (IOException e) {
                                SharedMethods.displayDebugLogMessage("test_drive", "Exception => " + e.getMessage());
                            }
                        }).start();
                    } else {
                        state.setTravelogueFolderId(this, travelogueFolderId1.get());
                        sharedPrefManager.saveString(Constants.DRIVE_FOLDER_DATABASE_KEY, travelogueFolderId1.get());
                    }
                });

                state.isTravelling(travelling -> {
                    if (travelling.get()) {
                        sharedPrefManager.saveBool("Travelling", travelling.get());
                        state.getCurrentTravel(currentTravel -> {
                            sharedPrefManager.saveString("CurrentTravel", currentTravel.get());
                            Log.d("test", sharedPrefManager.getString("CurrentTravel"));
                            if (!LocationService.isServiceRunning && sharedPrefManager.getBool("Travelling") && sharedPrefManager.getBool("AutoGps")) {
                                Intent intent = new Intent(MainActivity.this, LocationService.class);
                                intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
                                intent.putExtra("timeBetweenUpdate", sharedPrefManager.getLong("TimeBetweenAutoGps"));
                                MainActivity.this.startService(intent);
                            }
                        });
                    } else {
                        sharedPrefManager.saveBool("Travelling", false);
                        sharedPrefManager.saveString("CurrentTravel", null);
                    }
                });

                File cacheExportFolder = new File(getCacheDir(), "/export/");
                if (!cacheExportFolder.exists()) {
                    if (cacheExportFolder.mkdir()) {
                        Boolean isCreated = cacheExportFolder.mkdirs();
                    }
                }

                File cacheImagesFolder = new File(getCacheDir(), "/images/");
                if (!cacheImagesFolder.exists()) {
                    if (cacheImagesFolder.mkdir()) {
                        Boolean isCreated = cacheImagesFolder.mkdirs();
                    }
                }
                Intent homeActivity = new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(homeActivity);
                finish();
            });
        } else {
            Intent welcomeActivity = new Intent(this, WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(welcomeActivity);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        threadRunning = false;
        if (connectivityCheckThread != null) {
            connectivityCheckThread.interrupt();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!threadRunning) {
            threadRunning = true;
            connectivityCheckThread = new Thread(() -> {
                while (threadRunning) {
                    if (!NetworkConnectivityCheck.isNetworkAvailableAndConnected(this)) {
                        Intent intent = new Intent(this, NoConnection.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                        break;
                    }

                    try {
                        Thread.sleep(Constants.TIME_CHECK_CONNECTION);
                    } catch (InterruptedException e) {
                        threadRunning = false;
                    }
                }
            });
            connectivityCheckThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadRunning = false;
        if (connectivityCheckThread != null) {
            connectivityCheckThread.interrupt();
        }
        sharedPrefManager.updateBool("PermissionsRequested", false);
    }
}
