package com.afundacionfp.street_fight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class SearchClanActivity extends AppCompatActivity {

    private EditText inputJoinClanId;
    private String username;
    private String passwordSha;
    String from;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        passwordSha = intent.getStringExtra("password_sha");
        from = intent.getStringExtra("from");

        setContentView(R.layout.search_clan_layout_tmp);

        this.inputJoinClanId = findViewById(R.id.input_join_clan_id);
    }

    public void onButtonJoinClanClick(View v) {
        final String idClanStr = inputJoinClanId.getText().toString();
        Integer idClan = null;

        try {
            idClan = Integer.parseInt(idClanStr);
        } catch (NumberFormatException ignored) {}

        if (idClan != null) {
            if (from.equals("register")) {
                Client.getInstance(this).sendRegisterJoinClanRest(username, passwordSha, idClan, new ResponseHandlerObject() {
                    @Override
                    public void onOkResponse(JSONObject okResponseJson) {
                        // FALTA GUARDAR LA COOKIE EN LA PERSISTENCIA, ASÍ COMO EL ID DEL USUARIO Y LA FECHA DE EXPIRACIÓN DE LA SESIÓN
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onErrorResponse(JSONObject errorResponseJson) {
                        //assert errorResponseJson != null;
                        parseErrorResponse(errorResponseJson);
                    }
                });
            } else if (from.equals("detail")) {
                // SOLO NOS UNIMOS AL CLAN /user/{username}/clan/{id_clan} (POST)
            }
        } else {
            Toast.makeText(getApplicationContext(), "Introduce un id de clan válido", Toast.LENGTH_SHORT).show();
        }
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
