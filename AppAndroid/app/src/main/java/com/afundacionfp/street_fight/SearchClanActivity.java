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

public class SearchClanActivity extends AppCompatActivity {

    private EditText inputJoinClanId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String passwordSha = intent.getStringExtra("password_sha");

        setContentView(R.layout.search_clan_layout_tmp);

        this.inputJoinClanId = findViewById(R.id.input_join_clan_id);

        ImageButton buttonJoinClan = findViewById(R.id.button_join_clan);
        buttonJoinClan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idClanStr = inputJoinClanId.getText().toString();
                Integer idClan = null;

                try {
                    idClan = Integer.parseInt(idClanStr);
                } catch (NumberFormatException ignored) {}

                if (idClan != null) {
                    sendRegisterRest(username, passwordSha, idClan);
                } else {
                    Toast.makeText(getApplicationContext(), "Introduce un id de clan válido", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // COMANDO PYTHON: python manage.py runserver <LA_IP_DE_TU_PC>:8000
    private void sendRegisterRest(String username, String passwordSha, int idClan) {
        Log.d("# REGISTER REST", username + ", " + passwordSha + ", " + idClan);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://192.168.111.111:8000/user";

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
            Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show();
        }
    }

}
