package com.afundacionfp.street_fight;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class coordinates {
    public static Resources resources;

    public static void coors(MapView map) {
        for (int i = 0; i < 1; i++) {
            List<Marker> startMarker = new ArrayList<>();
            //  Add coordinates
            startMarker.add(new Marker(map));

            startMarker.get(i).setPosition(coors_num(i));
            startMarker.get(i).setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.get(i).setIcon(icons(i));
            startMarker.get(i).setTitle("\t" + coors_name(i) + "\t" + "\n" + coors_desc(i));
            map.getOverlays().add(startMarker.get(i));
        }
    }

    public static GeoPoint coors_num(int i) {
        List<GeoPoint> coors_num = new ArrayList<>();
        //  Add coordinate number
        coors_num.add(new GeoPoint(43.36209, -8.41248));    // Casa de la ciencias
        coors_num.add(new GeoPoint(43.36367, -8.404));

        return coors_num.get(i);
    }

    public static String coors_name(int i) {
        List<String> coors_name = new ArrayList<>();
        //  Add coordinate name
        coors_name.add("Casa de las ciencias");

        return coors_name.get(i);
    }

    public static String coors_desc(int i) {
        List<String> coors_desc = new ArrayList<>();
        //  Add coordinate description
        coors_desc.add("Casa en la que ");

        return coors_desc.get(i);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable icons(int i) {
        List<Drawable> icons = new ArrayList<>();
        // Add coordinate icon
        icons.add(resources.getDrawable(R.drawable.ic_torchlight_help_icon, null));


        return icons.get(i);
    }
}
