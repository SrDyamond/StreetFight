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

    private void sendRegisterRest(String username, String passwordSha, int idClan) {
        Log.d("# REGISTER REST", username + ", " + passwordSha + ", " + idClan);
    }

}
