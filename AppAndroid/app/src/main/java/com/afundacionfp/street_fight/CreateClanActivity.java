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

public class CreateClanActivity extends AppCompatActivity {

    EditText inputCreateClanName;
    EditText inputCreateClanAcronym;
    EditText inputCreateClanColor;
    EditText inputCreateClanUrlIcon;
    String username;
    String passwordSha;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        passwordSha = intent.getStringExtra("password_sha");

        setContentView(R.layout.create_clan_layout);

        inputCreateClanName = findViewById(R.id.input_create_clan_name);
        inputCreateClanAcronym = findViewById(R.id.input_create_clan_acronym);
        inputCreateClanColor = findViewById(R.id.input_create_clan_color);
        inputCreateClanUrlIcon = findViewById(R.id.input_create_clan_url_icon);

    }
    public void onButtonCreateClanClick(View v){
        final String clanName = inputCreateClanName.getText().toString();
        final String clanAcronym = inputCreateClanAcronym.getText().toString();
        final String clanColor = inputCreateClanColor.getText().toString();
        final String clanUrlIcon = inputCreateClanUrlIcon.getText().toString();

        if (!clanName.equals("") && !clanColor.equals("")) {
            if (clanColor.charAt(0) == '#' && clanColor.length() == 7) {
                Client.getInstance(this).sendRegisterCreateClanRest(username, passwordSha, clanName, clanAcronym, clanColor, clanUrlIcon, new ResponseHandlerObject() {
                    @Override
                    public void onOkResponse(JSONObject okResponseJson) {
                        // FALTA GUARDAR LA COOKIE EN LA PERSISTENCIA, ASÍ COMO EL ID DEL USUARIO Y LA FECHA DE EXPIRACIÓN DE LA SESIÓN
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onErrorResponse(JSONObject errorResponseJson) {
                        parseErrorResponse(errorResponseJson);
                    }
                });
            } else {
                Toast.makeText(CreateClanActivity.this, "El color debe ser en formato hexadecimal", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CreateClanActivity.this, "Los campos nombre y el color son requeridos", Toast.LENGTH_SHORT).show();
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
