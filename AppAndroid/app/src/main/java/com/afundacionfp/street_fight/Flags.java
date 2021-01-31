package com.afundacionfp.street_fight;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;

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

    public void sendFlagRequest(double latitude,double longitude){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://"+MainActivity.DJANGOSERVERIP+"/flag?latitude="+String.format("%1$,.7f", latitude)+"&longitude="+String.format("%1$,.7f", longitude)+"&radius="+String.format("%1$,.7f", radius);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("########RESPONSE", response.toString());
                        parseResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String responseBodyString = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject errorResponseBodyJson = null;
                        try {
                            errorResponseBodyJson = new JSONObject(responseBodyString);
                        } catch (JSONException e) {
                            // e.printStackTrace();
                            Log.d("########ERROR-L1", error.toString());
                            Log.d("########ERROR-L2", responseBodyString);
                        }
                        assert errorResponseBodyJson != null;
                        Log.d("########ERROR-JSON", errorResponseBodyJson.toString());
                        Toast.makeText(context, "Error al obtener las banderas", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    private void parseResponse(JSONArray jsonArrayFlags){
        for (int i=0;i<jsonArrayFlags.length();i++){
            Double flagLatitude=null;
            Double flagLongitude=null;
            try {
                Log.d("#Flag", ((JSONObject)jsonArrayFlags.get(i)).toString());
                flagLatitude=(double)((JSONObject)jsonArrayFlags.get(i)).get("latitude");
                flagLongitude=(double)((JSONObject)jsonArrayFlags.get(i)).get("longitude");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert flagLatitude != null;
            assert flagLongitude != null;
            Marker flag=new Marker(mapView);
            flag.setPosition(new GeoPoint(flagLatitude,flagLongitude));
            flag.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            flag.setIcon(ResourcesCompat.getDrawable(resources,R.drawable.ic_torchlight_help_icon,null));
            flag.setTitle("Casa de las ciencias");
            mapView.getOverlays().add(flag);
        }

    }

}
