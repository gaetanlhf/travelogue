package fr.insset.ccm.m1.sag.travelogue.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Travel;

public class NewTravelActivity extends AppCompatActivity {

    private TextView travelName;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.create_new_travel));
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_new_travel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void onClickSaveTravel(View view) {
        travelName = findViewById(R.id.activity_new_travel_edittext);
        if (!TextUtils.isEmpty(travelName.getText().toString())) {
            Travel newTravel = new Travel(mAuth.getCurrentUser().getUid());
            newTravel.createTravel(travelName.getText().toString());
            finish();
        }
    }
}