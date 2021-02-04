package com.afundacionfp.street_fight;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class Flags {

    private Context context;
    private Resources resources;
    private MapView mapView;
    private double latitude, longitude;
    private double radius=0.0051113;
   // private double radius=0.1;
    //private List<Marker> markerList;

    public Flags(Context context,Resources resources, MapView mapView, double latitude, double longitude) {
        this.context= context;
        this.resources = resources;
        this.mapView = mapView;
        this.latitude = latitude;
        this.longitude = longitude;
        sendFlagRequest(latitude, longitude);
    }

    public void sendFlagRequest(double latitude, double longitude){
        Client.getInstance(context).sendFlagRequest(latitude, longitude, radius, new ResponseHandlerArray() {
            @Override
            public void onOkResponse(JSONArray okResponseJson) {
                parseResponse(okResponseJson);
            }

            @Override
            public void onErrorResponse(JSONObject errorResponseJson) {
                Toast.makeText(context, "Error al obtener las banderas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseResponse(JSONArray jsonArrayFlags){
        for (int i=0;i<jsonArrayFlags.length();i++){
            Double flagLatitude=null;
            Double flagLongitude=null;
            try {
//                Log.d("#Flag", jsonArrayFlags.getJSONObject(i).toString());
                flagLatitude = jsonArrayFlags.getJSONObject(i).getDouble("latitude");
                flagLongitude = jsonArrayFlags.getJSONObject(i).getDouble("longitude");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert flagLatitude != null;
            assert flagLongitude != null;
            try {
                Marker flag = new Marker(mapView);
                flag.setPosition(new GeoPoint(flagLatitude, flagLongitude));
                flag.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                flag.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_torchlight_help_icon, null));
                flag.setTitle("Casa de las ciencias");
                mapView.getOverlays().add(flag);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

    }

}
