package com.afundacionfp.street_fight;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class FlagManagement {

    public static double showRadius = 0.0051113;
    public static double captureRadius = 0.0001113;

    private final Context context;
    private final Resources resources;
    private final MapView mapView;

    private List<FlagDTO> flagsToCapture;

    public FlagManagement(Context context, Resources resources, MapView mapView) {
        this.context= context;
        this.resources = resources;
        this.mapView = mapView;
    }

    public void sendFlagRequest(double userLatitude, double userLongitude) {

        Client.getInstance(context).sendFlagRequest(userLatitude, userLongitude, showRadius, new ResponseHandlerArray() {
            @Override
            public void onOkResponse(JSONArray okResponseJson) {
                parseResponse(okResponseJson, userLatitude, userLongitude);
            }
            @Override
            public void onErrorResponse(JSONObject errorResponseJson) {
                Toast.makeText(context, "Error al obtener las banderas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseResponse(JSONArray jsonArrayFlags, double userLatitude, double userLongitude) {
        for (int i = 0; i < jsonArrayFlags.length(); i++){

            Integer flagId = null;
            Double flagLatitude = null;
            Double flagLongitude = null;
            Boolean flagCapturing = null;
            ClanDTO clanDTO = null;

            try {
//                Log.d("#Flag", jsonArrayFlags.getJSONObject(i).toString());
                flagId = jsonArrayFlags.getJSONObject(i).getInt("id");
                flagLatitude = jsonArrayFlags.getJSONObject(i).getDouble("latitude");
                flagLongitude = jsonArrayFlags.getJSONObject(i).getDouble("longitude");
                flagCapturing = jsonArrayFlags.getJSONObject(i).getBoolean("capturing");
                if (jsonArrayFlags.getJSONObject(i).has("clan")) {
                    JSONObject flagClanJSON = jsonArrayFlags.getJSONObject(i).getJSONObject("clan");
                    String clanUrlIcon = flagClanJSON.getString("url_icon");
                    String clanAcronym = flagClanJSON.getString("acronym");
                    String clanColor = flagClanJSON.getString("color");
                    clanDTO = new ClanDTO(clanUrlIcon, clanAcronym, clanColor);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            assert flagId != null;
            assert flagLatitude != null;
            assert flagLongitude != null;
            assert flagCapturing != null;

            FlagDTO flagDTO = new FlagDTO(flagId, flagLatitude, flagLongitude, flagCapturing, clanDTO);

            Marker flag = new Marker(mapView);
            flag.setPosition(new GeoPoint(flagDTO.getLatitude(), flagDTO.getLongitude()));
            flag.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            if (flagDTO.getClan() == null && !flagDTO.isCapturing()){
                // bandera libre
                flag.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.flag_blank, null));
                flag.setTitle("Bandera libre");
            }else if (flagDTO.getClan() != null && !flagDTO.isCapturing()){
                // bandera capturada
                flag.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.flag_captured, null));
                flag.setTitle("Bandera de " + flagDTO.getClan().getAcronym());
            }else {
                // bandera en disputa
                flag.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.flag_capturing, null));
                flag.setTitle("Batalla en curso por la zona");
            }

            double distanceToLocation = Math.pow(flagDTO.getLatitude() - userLatitude, 2) + Math.pow(flagDTO.getLongitude() - userLongitude, 2);
            if (distanceToLocation <= captureRadius) {
                this.flagsToCapture = new ArrayList<>();
                this.flagsToCapture.add(flagDTO);
            }

            //TODO:Añadir descripcion a doc y restfacade
            mapView.getOverlays().add(flag);
        }

    }

    public List<FlagDTO> getFlagsToCapture() {
        return flagsToCapture;
    }
}
