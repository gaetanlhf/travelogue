package fr.insset.ccm.m1.sag.travelogue.activity;

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

import java.util.concurrent.atomic.AtomicReference;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.fragment.HomeFragment;
import fr.insset.ccm.m1.sag.travelogue.fragment.SettingsFragment;
import fr.insset.ccm.m1.sag.travelogue.fragment.TravelsFragment;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedPrefManager;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;

    private Fragment fragment = null;
    private Fragment oldFragment = null;
    private FragmentRefreshListener fragmentRefreshListener;
    private SharedPrefManager sharedPrefManager;
    private Thread connectivityCheckThread;
    private volatile boolean threadRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_home);
        threadRunning = true;
        connectivityCheckThread = new Thread(() -> {
            while (threadRunning) {
                if (!NetworkConnectivityCheck.isNetworkAvailableAndConnected(this)) {
                    Intent intent = new Intent(this, NoConnection.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
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
        sharedPrefManager = SharedPrefManager.getInstance(this);
        PermissionHelper.verifyPermissions(this);
        bottomNavigationView = findViewById(R.id.home_activity_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        fragment = oldFragment = homeFragment();
        loadFragment(fragment, oldFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AtomicReference<Boolean> fragmentReload = new AtomicReference<>(true);
        if (!sharedPrefManager.getBool("PermissionsRequested")) {
            PermissionHelper.verifyPermissions(this);
        }
        if (!threadRunning) {
            threadRunning = true;
            connectivityCheckThread = new Thread(() -> {
                while (threadRunning) {
                    if (!NetworkConnectivityCheck.isNetworkAvailableAndConnected(this)) {
                        Intent intent = new Intent(this, NoConnection.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        finish();
                        break;
                    } else {
                        if (getFragmentRefreshListener() != null && fragmentReload.get()) {
                            fragmentReload.set(false);
                            getFragmentRefreshListener().onRefresh();
                        }
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
    protected void onPause() {
        super.onPause();
        threadRunning = false;
        if (connectivityCheckThread != null) {
            connectivityCheckThread.interrupt();
        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!PermissionHelper.arePermissionsGranted(requestCode, permissions, grantResults)) {
            if (!sharedPrefManager.getBool("PermissionsRequested")) {
                sharedPrefManager.updateBool("PermissionsRequested", true);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.alert_not_all_permissions_granted_title)
                        .setMessage(R.string.alert_not_all_permissions_granted_desc)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            PermissionHelper.verifyPermissions(this);
                            sharedPrefManager.updateBool("PermissionsRequested", false);
                        })
                        .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            finish();
                        });
                builder.show();
            }
        } else {
            // Les permissions ont été accordées.
            sharedPrefManager.updateBool("PermissionsRequested", false);
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


    public interface FragmentRefreshListener {
        void onRefresh();
    }
}