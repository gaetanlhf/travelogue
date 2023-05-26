package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.adapter.SliderAdapter;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionHelper;

public class WelcomeActivity extends AppCompatActivity {

    int[] layouts;
    Button nextBtn, skipBtn;
    SliderAdapter sliderAdapter;
    private FirebaseAuth mAuth;
    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private TextView[] dotsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.welcome_activity_view_pager);
        linearLayout = findViewById(R.id.welcome_activity_linear_layout);
        nextBtn = findViewById(R.id.welcome_activity_next_button);
        skipBtn = findViewById(R.id.welcome_activity_skip_button);

        skipBtn.setOnClickListener(view -> {
            Intent loginActivity = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(loginActivity);
            finish();
        });

        nextBtn.setOnClickListener(view -> {
            int currentPage = viewPager.getCurrentItem() + 1;
            if (currentPage < layouts.length) {
                viewPager.setCurrentItem(currentPage);
            } else {
                if(PermissionHelper.areAllBasicPermissionsGranted(this)) {
                    Intent loginActivity = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                } else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                            .setTitle("Before going on")
                            .setMessage("You must grant permissions to the application for the features described in this screen to work.\n" +
                                    "You can then log in.")
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                PermissionHelper.verifyPermissions(this);
                            });
                    builder.show();
                }


            }

        });

        layouts = new int[]{R.layout.welcome_slide_1, R.layout.welcome_slide_2, R.layout.welcome_slide_3, R.layout.welcome_slide_4, R.layout.welcome_slide_5};
        sliderAdapter = new SliderAdapter(layouts, this);
        viewPager.setAdapter(sliderAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == layouts.length - 1) {
                    nextBtn.setText(R.string.login_activity_title);
                } else {
                    nextBtn.setText(R.string.welcome_activity_next_button);
                }
                setDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setDots(0);

    }

    private void setDots(int page) {
        linearLayout.removeAllViews();
        dotsTextView = new TextView[layouts.length];
        for (int i = 0; i < dotsTextView.length; i++) {
            dotsTextView[i] = new TextView(this);
            dotsTextView[i].setText(HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY));
            dotsTextView[i].setTextSize(30);
            dotsTextView[i].setTextColor(Color.parseColor("#a9b4bb"));
            linearLayout.addView(dotsTextView[i]);
        }

        if (dotsTextView.length > 0) {
            dotsTextView[page].setTextColor(Color.parseColor("#ffffff"));
        }
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