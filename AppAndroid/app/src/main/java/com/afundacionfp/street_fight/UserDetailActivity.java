package com.afundacionfp.street_fight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class UserDetailActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.user_detail_layout);

        ImageButton buttonGotoChangeClan = findViewById(R.id.userdetails_changeclan_button);
        buttonGotoChangeClan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchClanActivity.class);
                assert MainActivity.user != null;
                intent.putExtra("username", MainActivity.user.getUsername());
                intent.putExtra("session_cookie", MainActivity.user.getSessionCookie());
                intent.putExtra("from", "detail");
                startActivity(intent);
            }
        });

        ImageButton buttonGotoCreateClan = findViewById(R.id.userdetails_createclan_button);
        buttonGotoCreateClan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateClanActivity.class);
                intent.putExtra("username", MainActivity.user.getUsername());
                intent.putExtra("session_cookie", MainActivity.user.getSessionCookie());
                intent.putExtra("from", "detail");
                startActivity(intent);
            }
        });
    }

}
