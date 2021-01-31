package com.afundacionfp.street_fight;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class Coordinates {
    public static Resources resources;
    public static void coors(MapView map) {
        for (int i = 0; i < 1; i++) {
            List<Marker> startMarker = new ArrayList<>();
            //  Add coordinates

            startMarker.add(new Marker(map));
            startMarker.get(i).setPosition(coors_num(i));
            startMarker.get(i).setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.get(i).setIcon(coors_icons(i));
            startMarker.get(i).setTitle("\t" + coors_name(i) + "\t" + "\n" + coors_desc(i));
            map.getOverlays().add(startMarker.get(i));


        }
    }
    public static void ubicacion(double latitude, double longitude) {
        //http://192.168.0.11:8000/flag?latitude=43.38124&longitude=-8.40901&radius=0.0000013
        String url ="http://localhost:8000/flag?latitude="+latitude+"&longitude="+longitude+"&radius=0.0000013";;
        //RequestQueue queue = Volley.newRequestQueue(this);

    }

    public static GeoPoint coors_num(int i) {
        List<GeoPoint> coors_num = new ArrayList<>();
        //  Add coordinate number
        coors_num.add(new GeoPoint(43.38124, -8.40901));    // Casa de las ciencias
        return coors_num.get(i);
    }

    public static String coors_name(int i) {
        List<String> coors_name = new ArrayList<>();
        //  Add coordinate name
        coors_name.add("Casa de marcela"); // Casa de las ciencias

        return coors_name.get(i);
    }

    public static String coors_desc(int i) {
        List<String> coors_desc = new ArrayList<>();
        //  Add coordinate description
        coors_desc.add("Casa en la que ");  // Casa de las ciencias

        return coors_desc.get(i);
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable coors_icons(int i) {
        List<Drawable> coors_icons = new ArrayList<>();
        // Add coordinate icon
        coors_icons.add(resources.getDrawable(R.drawable.ic_torchlight_help_icon, null));   // Casa de las ciencias


        return coors_icons.get(i);
    }
}
