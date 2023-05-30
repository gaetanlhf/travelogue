package fr.insset.ccm.m1.sag.travelogue.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import fr.insset.ccm.m1.sag.travelogue.R;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BottomSheetPoint extends BottomSheetDialogFragment {
    private static final String ARG_LINKEDDATATYPE = "linkeddatatype";
    private static final String ARG_LINKEDDATA = "linkeddata";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_TIMESTAMP = "timestamp";

    private String linkedDataType;
    private String linkedData;
    private double longitude;
    private double latitude;
    private String timestamp;


    public static BottomSheetPoint newInstance(String linkedDataType, String linkedData, double longitude, double latitude, String timestamp) {
        final BottomSheetPoint fragment = new BottomSheetPoint();
        final Bundle args = new Bundle();
        args.putString(ARG_LINKEDDATATYPE, linkedDataType);
        args.putString(ARG_LINKEDDATA, linkedData);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putDouble(ARG_LATITUDE, latitude);
        args.putString(ARG_TIMESTAMP, timestamp);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            linkedDataType = getArguments().getString(ARG_LINKEDDATATYPE);
            linkedData = getArguments().getString(ARG_LINKEDDATA);
            longitude = getArguments().getDouble(ARG_LONGITUDE);
            latitude = getArguments().getDouble(ARG_LATITUDE);
            timestamp = getArguments().getString(ARG_TIMESTAMP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottom_sheet_point, container, false);

        TextView titleTextView = v.findViewById(R.id.fragment_bottom_sheet_point_title);
        Log.d("test", linkedDataType);
        switch (linkedDataType) {
            case "none":
                titleTextView.setText(R.string.gps_point);
                break;
            case "text":
                titleTextView.setText(R.string.text_gps_point);
                TextView textViewText = v.findViewById(R.id.fragment_bottom_sheet_point_text);
                textViewText.setVisibility(View.VISIBLE);
                textViewText.setText(linkedData);
                break;
            case "photo":
                ProgressBar spinner = v.findViewById(R.id.fragment_bottom_sheet_spinner);
                spinner.setVisibility(View.VISIBLE);
                titleTextView.setText(R.string.image_gps_point);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                ImageView imageView = v.findViewById((R.id.fragment_bottom_sheet_point_image));
                StorageReference storageRef = storage.getReference().child(linkedData);


                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    spinner.setVisibility(View.GONE);
                    Picasso.get().load(uri).into(imageView);
                }).addOnFailureListener(exception -> {
                    spinner.setVisibility(View.GONE);
                    TextView textViewError = v.findViewById(R.id.fragment_bottom_sheet_point_text);
                    textViewError.setVisibility(View.VISIBLE);
                    textViewError.setText(R.string.bottom_sheet_point_no_image);
                });
                imageView.setVisibility(View.VISIBLE);
                break;
        }

        TextView textViewPosition = v.findViewById(R.id.fragment_bottom_sheet_point_position);
        textViewPosition.setText(R.string.longitude + " " + longitude + ", " + R.string.latitude + latitude);

        TextView textViewTime = v.findViewById(R.id.fragment_bottom_sheet_point_time);
        textViewTime.setText(getDate(timestamp));


        return v;
    }


    public String getDate(String timestampStr) {
        long timestamp;
        timestamp = Long.parseLong(timestampStr) * 1000;
        Locale currentLocale = Locale.getDefault();
        TimeZone currentTimezone = TimeZone.getDefault();
        Date date = new Date(timestamp);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, currentLocale);
        dateFormat.setTimeZone(currentTimezone);
        String dateString = dateFormat.format(date);
        return dateString;
    }



}