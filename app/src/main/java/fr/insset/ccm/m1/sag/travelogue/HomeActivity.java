package fr.insset.ccm.m1.sag.travelogue;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import fr.insset.ccm.m1.sag.travelogue.db.InitDatabase;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        InitDatabase initDatabase = new InitDatabase(mAuth.getCurrentUser().getUid());
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Fragment fragment = homeFragment(null);
        loadFragment(fragment);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.home:
                fragment = homeFragment(fragment);
                break;
            case R.id.travels:
                fragment = travelsFragment(fragment);
                break;
            case R.id.settings:
                fragment = settingsFragment(fragment);
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

    Fragment homeFragment(Fragment fragment) {
        fragment = new HomeFragment();
        getSupportActionBar().setTitle(getResources().getString(R.string.home_text));
        return fragment;
    }

    Fragment travelsFragment(Fragment fragment) {
        fragment = new TravelsFragment();
        getSupportActionBar().setTitle(getResources().getString(R.string.travels_text));
        return fragment;
    }

    Fragment settingsFragment(Fragment fragment) {
        fragment = new SettingsFragment();
        getSupportActionBar().setTitle(getResources().getString(R.string.settings_text));
        return fragment;
    }

}