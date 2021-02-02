package com.afundacionfp.street_fight;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Client {

    private static Client client = null;
    public static final String DJANGOSERVERIP ="192.168.0.105:8000";
    private RequestQueue requestQueue;

    private Client(Context context){
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public static Client getInstance(Context context){
        if (client == null) {
            client = new Client(context);
        }
        return client;
    }

    private static String calculateSHA1(String password) throws NoSuchAlgorithmException {
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(password.getBytes(StandardCharsets.UTF_8));

        return new BigInteger(1, crypt.digest()).toString(16);
    }

    public void sendLoginRest(String username, String password, ResponseHandler handler) {
        String passwordSha = null;
        try {
            passwordSha = calculateSHA1(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String url = "http://" + DJANGOSERVERIP + "/user/" + username + "/session";

        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("password_sha", passwordSha);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBodyJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject okResponseJson) {
                        Log.d("# RESPONSE", okResponseJson.toString());
                        handler.onOkResponse(okResponseJson);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String responseBodyString = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject errorResponseJson = null;
                        try {
                            errorResponseJson = new JSONObject(responseBodyString);
                        } catch (JSONException e) {
                            // e.printStackTrace();
                            Log.d("## ERROR-L1", error.toString());
                            Log.d("## ERROR-L2", responseBodyString);
                        }
                        Log.d("# ERROR-JSON", errorResponseJson.toString());
                        handler.onErrorResponse(errorResponseJson);
                    }
                });

        // Add the request to the RequestQueue.
        this.requestQueue.add(jsonObjectRequest);
    }

}
