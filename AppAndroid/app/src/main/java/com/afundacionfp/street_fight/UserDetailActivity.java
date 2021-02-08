package com.afundacionfp.street_fight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class UserDetailActivity extends AppCompatActivity {

    private TextView textViewUsername;
    private TextView textViewClanName;
    private ImageView imageViewCalnIcon;

    private URL urlIcon = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.user_detail_layout);

        textViewUsername = findViewById(R.id.userdetails_text_username);
        textViewUsername.setText(UserPreferences.getInstance().getUsername(getApplicationContext()));

        textViewClanName = findViewById(R.id.userdetails_text_clan_name);
        imageViewCalnIcon = findViewById(R.id.userdetails_clan_icon);

        ImageDownloaderThread imageDownloaderThread = new ImageDownloaderThread(imageViewCalnIcon, this);

        Client.getInstance(this).sendGetUserInfo(UserPreferences.getInstance().getUsername(getApplicationContext()), new ResponseHandlerObject() {
            @Override
            public void onOkResponse(JSONObject okResponseJson) {
                try {
                    String username_text = okResponseJson.getString("name") + " (" + okResponseJson.getInt("captured_flags") + ")";
                    textViewUsername.setText(username_text);
                    textViewClanName.setText(okResponseJson.getJSONObject("clan").getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    urlIcon = new URL(okResponseJson.getJSONObject("clan").getString("url_icon"));
                } catch (MalformedURLException | JSONException e) {
                    e.printStackTrace();
                }
                imageDownloaderThread.setUrl(urlIcon);
                imageDownloaderThread.start();
            }

            @Override
            public void onErrorResponse(JSONObject errorResponseJson) {
                parseErrorResponse(errorResponseJson);
            }
        });
    }

    public void onLogoutClick(View v){
        Client.getInstance(this).sendDeleteSession(UserPreferences.getInstance().getUsername(this),
                UserPreferences.getInstance().getSessionCookie(this), new ResponseHandlerObject() {
                    @Override
                    public void onOkResponse(JSONObject okResponseJson) {
                        logout();
                    }

                    @Override
                    public void onErrorResponse(JSONObject errorResponseJson) {
                        parseErrorResponse(errorResponseJson);
                    }
                });
    }

    public void onChangeClanClick(View v) {
        Intent intent = new Intent(getApplicationContext(), SearchClanActivity.class);
        intent.putExtra("username", UserPreferences.getInstance().getUsername(getApplicationContext()));
        intent.putExtra("session_cookie", UserPreferences.getInstance().getSessionCookie(getApplicationContext()));
        intent.putExtra("from", "detail");
        startActivity(intent);
    }

    public void onCreateClanClick(View v) {
        Intent intent = new Intent(getApplicationContext(), CreateClanActivity.class);
        intent.putExtra("username", UserPreferences.getInstance().getUsername(getApplicationContext()));
        intent.putExtra("session_cookie", UserPreferences.getInstance().getSessionCookie(getApplicationContext()));
        intent.putExtra("from", "detail");
        startActivity(intent);
    }

    private void parseErrorResponse(JSONObject errorResponseBodyJson) {
        int errorCode = 0;

        try {
            errorCode = errorResponseBodyJson.getInt("error");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (errorCode) {
            case 4001:
                Toast.makeText(this, "Bad request", Toast.LENGTH_SHORT).show();
                break;
            case 4003:
                Toast.makeText(this, "Sesion no valida", Toast.LENGTH_SHORT).show();
                logout();
                break;
            case 4004:
                Toast.makeText(this, "El usuario no está registrado", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
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
