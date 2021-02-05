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

    private final Context context;
    private final Resources resources;
    private final MapView mapView;
    private List<FlagDTO> nearFlags;

    public FlagManagement(Context context, Resources resources, MapView mapView) {
        this.context= context;
        this.resources = resources;
        this.mapView = mapView;
        this.nearFlags = new ArrayList<>();
    }

    public void sendFlagRequest(double latitude, double longitude) {
        double radius = 0.0051113;
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

            try {
                Marker flag = new Marker(mapView);
                flag.setPosition(new GeoPoint(flagDTO.getLatitude(), flagDTO.getLongitude()));
                flag.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                if (flagDTO.getClan() == null && !flagDTO.isCapturing()){
                    flag.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.flag_blank, null));
                    flag.setTitle("Bandera libre");
                }else if (flagDTO.getClan() != null && !flagDTO.isCapturing()){
                    flag.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.flag_captured, null));
                    flag.setTitle("Bandera de " + flagDTO.getClan().getAcronym());
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
