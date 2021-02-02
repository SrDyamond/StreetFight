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

public class LoginActivity extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.login_layout);

        inputUsername = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_password);
    }

    public void onButtonLoginClick(View v) {
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        if (!username.equals("") && !password.equals("")) {
            //sendLoginRest(username, password);
            Client.getInstance(this).sendLoginRest(
                    username, password, new ResponseHandlerObject() {
                        @Override
                        public void onOkResponse(JSONObject okResponseJson) {
                            // FALTA GUARDAR LA COOKIE EN LA PERSISTENCIA, ASÍ COMO EL ID DEL USUARIO Y LA FECHA DE EXPIRACIÓN DE LA SESIÓN
                            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                            startActivity(intent);
                        }
                        @Override
                        public void onErrorResponse(JSONObject errorResponseJson) {
                            // assert errorResponseJson != null;
                            parseErrorResponse(errorResponseJson);
                        }
                    }
            );
        } else {
            Toast.makeText(getApplicationContext(), "Introduce usuario y contraseña", Toast.LENGTH_SHORT).show();
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
