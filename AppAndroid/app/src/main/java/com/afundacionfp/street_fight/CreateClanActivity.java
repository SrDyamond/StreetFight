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
    }

}
