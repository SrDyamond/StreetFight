package com.afundacionfp.street_fight;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.login_layout);

        EditText input_username = findViewById(R.id.input_username);
        EditText input_password = findViewById(R.id.input_password);

        ImageButton button_login = findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = input_username.getText().toString();
                String password = input_password.getText().toString();

                if (!username.equals("") && !password.equals("")) {
                    Log.d("############USER, PASSWORD", username + ", " + password);

                    String password_sha = null;
                    try {
                        password_sha = calculateSHA1(password);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = "http://192.168.111.111:8000/user/" + username + "/session";

                    JSONObject requestBodyJson = new JSONObject();
                    try {
                        requestBodyJson.put("password_sha", password_sha);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, requestBodyJson, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Log.d("########RESPONSE", response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String responseBodyString = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                JSONObject responseBodyJson = null;
                                try {
                                    responseBodyJson = new JSONObject(responseBodyString);
                                } catch (JSONException e) {
                                    // e.printStackTrace();
                                    Log.d("########ERROR-L1", error.toString());
                                    Log.d("########ERROR-L2", responseBodyString);
                                }
                                assert responseBodyJson != null;
                                Log.d("########ERROR-JSON", responseBodyJson.toString());
                            }
                        });


                    // Add the request to the RequestQueue.
                    queue.add(jsonObjectRequest);


                } else {
                    Toast.makeText(MainActivity.this, "Introduce usuario y contraseña", Toast.LENGTH_SHORT).show();
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

}