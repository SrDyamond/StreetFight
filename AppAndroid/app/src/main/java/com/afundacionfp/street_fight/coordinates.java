package com.afundacionfp.street_fight.Map;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.afundacionfp.street_fight.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class coordinates {
    public static void coors(MapView map) {
        for (int i = 0; i < 1; i++) {
            List<Marker> startMarker = new ArrayList<>();
            //  Add coordinates
            startMarker.add(new Marker(map));
            
            startMarker.get(i).setPosition(coors_num(i));
            startMarker.get(i).setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.get(i).setIcon(icons(i));
            startMarker.get(i).setTitle(coors_name(i));
            map.getOverlays().add(startMarker.get(i));
        }
    }

    public static GeoPoint coors_num(int i) {
        List<GeoPoint> coors_num = new ArrayList<>();
        //  Add coordinate numer
        coors_num.add(new GeoPoint(43.36367, -8.404));
        coors_num.add(new GeoPoint(43.36209, -8.41248));

        return coors_num.get(i);
    }

    public static Drawable icons(int i) {
        List<Drawable> icons = new ArrayList<>();
        // Add c
        icons.add(ContextCompat.getDrawable(null, R.drawable.ic_torchlight_help_icon));

        return icons.get(i);
    }

    public static String coors_name(int i) {
        List<String> coors_name = new ArrayList<>();
        coors_name.add("Casa de las ciencias");

        return coors_name.get(i);
    }
}
