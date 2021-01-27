package com.afundacionfp.street_fight;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
                } else {
                    Toast.makeText(MainActivity.this, "Introduce usuario y contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}