package fr.insset.ccm.m1.sag.travelogue.activity;

import static fr.insset.ccm.m1.sag.travelogue.Constants.ACCESS_BACKGROUND_LOCATION_PERMISSION;
import static fr.insset.ccm.m1.sag.travelogue.Constants.ACCESS_COARSE_LOCATION_PERMISSION;
import static fr.insset.ccm.m1.sag.travelogue.Constants.ACCESS_FINE_LOCATION_PERMISSION;
import static fr.insset.ccm.m1.sag.travelogue.Constants.BACKGROUND_LOCATION_PERMISSION_CODE;
import static fr.insset.ccm.m1.sag.travelogue.Constants.CAMERA_PERMISSION;
import static fr.insset.ccm.m1.sag.travelogue.Constants.CAMERA_PERMISSION_CODE;
import static fr.insset.ccm.m1.sag.travelogue.Constants.FOREGROUND_SERVICE_PERMISSION;
import static fr.insset.ccm.m1.sag.travelogue.Constants.LOCATION_PERMISSION_CODE;
import static fr.insset.ccm.m1.sag.travelogue.Constants.PERMISSION_SETTINGS_CODE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.fragment.HomeFragment;
import fr.insset.ccm.m1.sag.travelogue.fragment.SettingsFragment;
import fr.insset.ccm.m1.sag.travelogue.fragment.TravelsFragment;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionsHelper;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;

    private Fragment fragment = null;
    private Fragment oldFragment = null;
    private FragmentRefreshListener fragmentRefreshListener;
    private Thread networkCheckThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        super.onCreate(savedInstanceState);
        new Thread(() -> {
            NetworkConnectivityCheck.checkConnection(this);
        }).start();
        networkCheckThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(5000);
                    NetworkConnectivityCheck.checkConnection(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        networkCheckThread.start();


        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_home);
        PermissionHelper.verifyPermissions(this);
        bottomNavigationView = findViewById(R.id.home_activity_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        fragment = oldFragment = homeFragment();
        loadFragment(fragment, oldFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getFragmentRefreshListener() != null) {
            getFragmentRefreshListener().onRefresh();
        }
        PermissionHelper.verifyPermissions(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        oldFragment = fragment;
        switch (item.getItemId()) {
            case R.id.home:
                fragment = homeFragment();
                break;
            case R.id.travels:
                fragment = travelsFragment();
                break;
            case R.id.settings:
                fragment = settingsFragment();
                break;
        }
        if (fragment != null) {
            loadFragment(fragment, oldFragment);
        }
        return true;
    }

    void loadFragment(Fragment fragment, Fragment oldFragment) {
        if (fragment == oldFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_activity_relative_layout, fragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().detach(oldFragment).replace(R.id.home_activity_relative_layout, fragment).commit();
        }
    }

    Fragment homeFragment() {
        fragment = new HomeFragment();
        getSupportActionBar().setTitle(getResources().getString(R.string.home_text));
        return fragment;
    }

    Fragment travelsFragment() {
        fragment = new TravelsFragment();
        getSupportActionBar().setTitle(getResources().getString(R.string.travels_text));
        return fragment;
    }

    Fragment settingsFragment() {
        fragment = new SettingsFragment();
        getSupportActionBar().setTitle(getResources().getString(R.string.settings_text));
        return fragment;
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkCheckThread != null) {
            networkCheckThread.interrupt();
        }
    }

    public interface FragmentRefreshListener {
        void onRefresh();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!PermissionHelper.arePermissionsGranted(requestCode, permissions, grantResults)) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                    .setTitle("Nosdq")
                    .setMessage("qsdsdq")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        PermissionHelper.verifyPermissions(this);
                    })
                    .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                    });
            builder.show();
        } else {
            // Les permissions ont été accordées.
        }
    }
}