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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.login_layout);

        EditText inputUsername = findViewById(R.id.input_username);
        EditText inputPassword = findViewById(R.id.input_password);

        ImageButton buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();

                if (!username.equals("") && !password.equals("")) {
                    Log.d("############USER, PASSWORD", username + ", " + password);
                    sendLoginRest(username, password);
                } else {
                    Toast.makeText(getApplicationContext(), "Introduce usuario y contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static String calculateSHA1(String password) throws NoSuchAlgorithmException {
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(password.getBytes(StandardCharsets.UTF_8));

        return new BigInteger(1, crypt.digest()).toString(16);
    }

    // COMANDO PYTHON: python manage.py runserver <LA_IP_DE_TU_PC>:8000
    private void sendLoginRest(String username, String password) {
        String passwordSha = null;
        try {
            passwordSha = calculateSHA1(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://"+MainActivity.DJANGOSERVERIP+"/user/" + username + "/session";

        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("password_sha", passwordSha);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBodyJson,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("########RESPONSE", response.toString());
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
                        Log.d("########ERROR-L1", error.toString());
                        Log.d("########ERROR-L2", responseBodyString);
                    }
                    assert errorResponseBodyJson != null;
                    Log.d("########ERROR-JSON", errorResponseBodyJson.toString());
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

        switch (errorCode) {
            case 4002:
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                break;
            case 4004:
                Toast.makeText(this, "El usuario no está registrado", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
