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
//    private double latitude, longitude;
    private double radius=0.0051113;
   // private double radius=0.1;
    //private List<Marker> markerList;

    public Flags(Context context,Resources resources, MapView mapView) {
        this.context= context;
        this.resources = resources;
        this.mapView = mapView;
//        this.latitude = latitude;
//        this.longitude = longitude;
//        sendFlagRequest(latitude, longitude);
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
            Boolean capturing = null;
            JSONObject clan = null;
            try {
//                Log.d("#Flag", jsonArrayFlags.getJSONObject(i).toString());
                flagLatitude = jsonArrayFlags.getJSONObject(i).getDouble("latitude");
                flagLongitude = jsonArrayFlags.getJSONObject(i).getDouble("longitude");
                capturing = jsonArrayFlags.getJSONObject(i).getBoolean("capturing");
                if (jsonArrayFlags.getJSONObject(i).has("clan")) {
                    clan = jsonArrayFlags.getJSONObject(i).getJSONObject("clan");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert flagLatitude != null;
            assert flagLongitude != null;
            assert capturing != null;
            try {
                Marker flag = new Marker(mapView);
                flag.setPosition(new GeoPoint(flagLatitude, flagLongitude));
                flag.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                if (clan==null && !capturing){
                    flag.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.flag_blank, null));
                    flag.setTitle("Bandera libre");
                }else if (clan!=null && !capturing){
                    flag.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.flag_captured, null));
                    try {
                        flag.setTitle("Bandera de " + clan.getString("acronym"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    flag.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.flag_capturing, null));
                    flag.setTitle("Batalla en curso por la zona");
                }
                //TODO:Añadir descripcion a doc y restfacade
                mapView.getOverlays().add(flag);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

    }

}
