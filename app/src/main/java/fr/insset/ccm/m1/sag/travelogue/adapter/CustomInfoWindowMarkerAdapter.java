package fr.insset.ccm.m1.sag.travelogue.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.net.URL;

import fr.insset.ccm.m1.sag.travelogue.R;

public class CustomInfoWindowMarkerAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowMarkerAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.custom_info_window_marker, null);
        ImageView imgView = view.findViewById(R.id.photo);
        imgView.setImageResource(R.drawable.photo_test);

        return view;
    }


    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }
}
