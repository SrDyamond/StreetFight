package com.afundacionfp.street_fight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class CreateClanActivity extends AppCompatActivity {

    String username;
    String password_sha;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        this.username = intent.getStringExtra("username");
        this.password_sha = intent.getStringExtra("password_sha");

        setContentView(R.layout.create_clan_layout);

    }

}
