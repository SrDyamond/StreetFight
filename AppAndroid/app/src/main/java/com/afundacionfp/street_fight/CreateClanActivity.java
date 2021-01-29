package com.afundacionfp.street_fight;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CreateClanActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String passwordSha = intent.getStringExtra("password_sha");

        setContentView(R.layout.create_clan_layout);

        EditText inputCreateClanName = findViewById(R.id.input_create_clan_name);
        EditText inputCreateClanAcronym = findViewById(R.id.input_create_clan_acronym);
        EditText inputCreateClanColor = findViewById(R.id.input_create_clan_color);
        EditText inputCreateClanUrlIcon = findViewById(R.id.input_create_clan_url_icon);

        ImageButton buttonCreateClanSubmit = findViewById(R.id.button_create_clan_submit);
        buttonCreateClanSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String clanName = inputCreateClanName.getText().toString();
                final String clanAcronym = inputCreateClanAcronym.getText().toString();
                final String clanColor = inputCreateClanColor.getText().toString();
                final String clanUrlIcon = inputCreateClanUrlIcon.getText().toString();

                if (!clanName.equals("") && !clanColor.equals("")) {
                    if (clanColor.charAt(0) == '#' && clanColor.length() == 7) {
                        sendRegisterRest(username, passwordSha, clanName, clanAcronym, clanColor, clanUrlIcon);
                    } else {
                        Toast.makeText(CreateClanActivity.this, "El color debe ser en formato hexadecimal", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateClanActivity.this, "Debe rellenar como mínimo el nombre y el color", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendRegisterRest(String username, String passwordSha, String clanName, String clanAcronym, String clanColor, String clanUrlIcon) {
        Log.d("# REGISTER REST", "'" + username + "', '" + passwordSha + "', '" + clanName + "', '" + clanAcronym + "', '" + clanColor + "', '" + clanUrlIcon + "'");
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://192.168.111.111:8000/user";

        JSONObject requestBodyJson = new JSONObject();
        JSONObject newClanJson = new JSONObject();
        try {
            newClanJson.put("name", clanName);
            newClanJson.put("color", clanName);

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
                        // FALTA GUARDAR LA COOKIE EN LA PERSISTENCIA, ASÍ COMO EL ID DEL USUARIO Y LA FECHA DE EXPIRACIÓN DE LA SESIÓN
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(intent);
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
                        assert errorResponseBodyJson != null;
                        Log.d("## ERROR-JSON", errorResponseBodyJson.toString());
                        parseErrorResponse(errorResponseBodyJson);
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void parseErrorResponse(JSONObject errorResponseBodyJson) {
        int errorCode = 0;

        try {
            errorCode = errorResponseBodyJson.getInt("error");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (errorCode == 4005) {
            Toast.makeText(this, "Conflicto", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show();
        }
    }

}
