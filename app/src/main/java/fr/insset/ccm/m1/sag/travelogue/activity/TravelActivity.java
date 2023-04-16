package fr.insset.ccm.m1.sag.travelogue.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.entity.Travel;

public class TravelActivity extends AppCompatActivity {

    private Travel travel;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);
        Intent intent = getIntent();

        TextView travelName = findViewById(R.id.travel_name_textView);
        travelName.setText(intent.getStringExtra("travelName"));

        Button button = findViewById(R.id.btn_back_view_travel);
        button.setOnClickListener(v -> {
            finish();
        });
    }
}