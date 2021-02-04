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
    private String sessionCookie;
    String from;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        passwordSha = intent.getStringExtra("password_sha");
        sessionCookie = intent.getStringExtra("session_cookie");
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
                        try {
                            //UserPreferences.getInstance().setExpiration(okResponseJson.getString("expiration"),getApplicationContext());
                            UserPreferences.getInstance().savePreferences(
                                    getApplicationContext(),
                                    okResponseJson.getInt("user_id"),
                                    username,
                                    okResponseJson.getString("session_cookie"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onErrorResponse(JSONObject errorResponseJson) {
                        //assert errorResponseJson != null;
                        parseErrorResponse(errorResponseJson);
                    }
                });
            } else if (from.equals("detail")) {
                // SOLO NOS UNIMOS AL CLAN /user/{username}/clan/{id_clan} (POST)
                Client.getInstance(this).sendChangeClanRest(username, sessionCookie, idClan, new ResponseHandlerObject() {
                    @Override
                    public void onOkResponse(JSONObject okResponseJson) {
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onErrorResponse(JSONObject errorResponseJson) {
                        parseErrorResponse(errorResponseJson);
                    }
                });
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

        switch (errorCode) {
            case 4003:
                Toast.makeText(this, "Sesion invalida", Toast.LENGTH_SHORT).show();
                logout();
                break;
            case 4004:
                Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                logout();
                break;
            case 4005:
                Toast.makeText(this, "Conflicto", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Otro error", Toast.LENGTH_SHORT).show();
                break;
        }

    }
    public void logout(){
        UserPreferences.getInstance().deleteAll(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
