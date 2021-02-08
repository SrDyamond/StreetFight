package com.afundacionfp.street_fight;

import android.content.Context;
import android.util.Log;

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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Client {

    private static Client client = null;
    public static final String DJANGOSERVERIP = "192.168.0.20:8000";
    private final RequestQueue requestQueue;

    private Client(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public static Client getInstance(Context context) {
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

    public void sendLoginRest(String username, String password, ResponseHandlerObject handler) {
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

    public void sendRegisterJoinClanRest(String username, String passwordSha, int idClan, ResponseHandlerObject handler) {
        Log.d("# REGISTER REST", username + ", " + passwordSha + ", " + idClan);

        String url = "http://" + DJANGOSERVERIP + "/user";

        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("username", username);
            requestBodyJson.put("password_sha", passwordSha);
            requestBodyJson.put("join_clan_id", idClan);
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
        requestQueue.add(jsonObjectRequest);
    }

    public void sendRegisterCreateClanRest(String username, String passwordSha, String clanName, String clanAcronym, String clanColor, String clanUrlIcon, ResponseHandlerObject handler) {
        Log.d("# REGISTER REST", "'" + username + "', '" + passwordSha + "', '" + clanName + "', '" + clanAcronym + "', '" + clanColor + "', '" + clanUrlIcon + "'");
        // Instantiate the RequestQueue.
        String url = "http://" + DJANGOSERVERIP + "/user";

        JSONObject requestBodyJson = new JSONObject();
        JSONObject newClanJson = new JSONObject();
        try {
            newClanJson.put("name", clanName);
            newClanJson.put("color", clanColor);

            if (!clanAcronym.equals("")) {
                newClanJson.put("acronym", clanAcronym);
            }

            if (!clanUrlIcon.equals("")) {
                newClanJson.put("url_icon", clanUrlIcon);
            }

            requestBodyJson.put("username", username);
            requestBodyJson.put("password_sha", passwordSha);
            requestBodyJson.put("create_clan", newClanJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBodyJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("## RESPONSE", response.toString());
                        handler.onOkResponse(response);
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
                            Log.d("## ERROR-L1", error.toString());
                            Log.d("## ERROR-L2", responseBodyString);
                        }
                        // assert errorResponseBodyJson != null;
                        Log.d("## ERROR-JSON", errorResponseBodyJson.toString());
                        handler.onErrorResponse(errorResponseBodyJson);
                    }
                });

        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }

    public void sendCreateClanRest(int idUser, String username, String sessionCookie, String clanName, String clanAcronym,
                                   String clanColor, String clanUrlIcon, ResponseHandlerObject handler) {
        Log.d("# REGISTER REST", "'" + username + "', '" + sessionCookie + "', '" + clanName + "'," +
                " '" + clanAcronym + "', '" + clanColor + "', '" + clanUrlIcon + "'");
        // Instantiate the RequestQueue.
        String url = "http://" + DJANGOSERVERIP + "/clan";

        JSONObject newClanJson = new JSONObject();
        try {
            newClanJson.put("name", clanName);
            newClanJson.put("color", clanColor);
            newClanJson.put("founder_id", idUser);

            if (!clanAcronym.equals("")) {
                newClanJson.put("acronym", clanAcronym);
            }

            if (!clanUrlIcon.equals("")) {
                newClanJson.put("url_icon", clanUrlIcon);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestCustomHeader(Request.Method.POST, url, sessionCookie, newClanJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("## RESPONSE", response.toString());
                        handler.onOkResponse(response);
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
                            Log.d("## ERROR-L1", error.toString());
                            Log.d("## ERROR-L2", responseBodyString);
                        }
                        // assert errorResponseBodyJson != null;
                        Log.d("## ERROR-JSON", errorResponseBodyJson.toString());
                        handler.onErrorResponse(errorResponseBodyJson);
                    }
                });

        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }

    public void sendFlagRequest(double latitude, double longitude, double radius, ResponseHandlerArray handler) {
        // Instantiate the RequestQueue.
        String url = "http://" + DJANGOSERVERIP + "/flag?latitude=" + String.format(Locale.US, "%1$,.7f", latitude) + "&longitude=" + String.format(Locale.US, "%1$,.7f", longitude) + "&radius=" + String.format(Locale.US, "%1$,.7f", radius);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        Log.d("########RESPONSE", response.toString());
                        handler.onOkResponse(response);
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
//                            Log.d("########ERROR-L1", error.toString());
//                            Log.d("########ERROR-L2", responseBodyString);
                        }
                        //assert errorResponseBodyJson != null;
                        Log.d("# ERROR-JSON", errorResponseBodyJson.toString());
                        handler.onErrorResponse(errorResponseBodyJson);
                    }
                });

        // Add the request to the RequestQueue.
        requestQueue.add(jsonArrayRequest);
    }

    public void sendChangeClanRest(String username, String sessionCookie, int idClan, ResponseHandlerObject handler) {
        Log.d("# REGISTER REST", "'" + username + "', '" + sessionCookie);
        // Instantiate the RequestQueue.
        String url = "http://" + DJANGOSERVERIP + "/user/" + username + "/clan/" + idClan;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestCustomHeader(Request.Method.POST, url, sessionCookie, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("## RESPONSE", response.toString());
                        handler.onOkResponse(response);
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
                            Log.d("## ERROR-L1", error.toString());
                            Log.d("## ERROR-L2", responseBodyString);
                        }
                        // assert errorResponseBodyJson != null;
                        Log.d("## ERROR-JSON", errorResponseBodyJson.toString());
                        handler.onErrorResponse(errorResponseBodyJson);
                    }
                });

        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }

    public void sendGetUserInfo(String username, ResponseHandlerObject handler) {
        String url = "http://" + DJANGOSERVERIP + "/user/" + username;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
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

    public void sendDeleteSession(String username,String sessionCookie, ResponseHandlerObject handler) {
        String url = "http://" + DJANGOSERVERIP + "/user/" + username+"/session";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestCustomHeader(Request.Method.DELETE, url, sessionCookie, null,
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

    public void sendCatchFlag(String username,Integer id_flag, String sessionCookie, ResponseHandlerObject handler) {
        String url = "http://" + DJANGOSERVERIP + "/user/" + username + "/catch/" + id_flag;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestCustomHeader(Request.Method.DELETE, url, sessionCookie, null,
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
