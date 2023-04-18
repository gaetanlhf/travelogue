package fr.insset.ccm.m1.sag.travelogue.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.fragment.HomeFragment;
import fr.insset.ccm.m1.sag.travelogue.fragment.SettingsFragment;
import fr.insset.ccm.m1.sag.travelogue.fragment.TravelsFragment;
import fr.insset.ccm.m1.sag.travelogue.helper.db.InitDatabase;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;

    private Fragment fragment = null;
    private FragmentRefreshListener fragmentRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        InitDatabase initDatabase = new InitDatabase(mAuth.getCurrentUser().getUid());
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottom_nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        fragment = homeFragment();
        loadFragment(fragment);
        //SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.activity_home_refresh);
        //swipeRefreshLayout.setOnRefreshListener(
        //        () -> {
        //            if (getFragmentRefreshListener() != null) {
        //                getFragmentRefreshListener().onRefresh();
        //            }
        //            swipeRefreshLayout.setRefreshing(false);
        //        }
        //);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getFragmentRefreshListener() != null) {
            getFragmentRefreshListener().onRefresh();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            loadFragment(fragment);
        }
        return true;
    }

    void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.relativelayout, fragment).commit();
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

    public interface FragmentRefreshListener {
        void onRefresh();
    }

}