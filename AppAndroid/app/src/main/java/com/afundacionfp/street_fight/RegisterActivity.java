package com.afundacionfp.street_fight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText input_register_username;
    EditText input_register_password;
    EditText input_register_repeat_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.register_layout);

        input_register_username = findViewById(R.id.input_register_username);
        input_register_password = findViewById(R.id.input_register_password);
        input_register_repeat_password = findViewById(R.id.input_register_repeat_password);

        ImageButton button_register_join_clan = findViewById(R.id.button_register_join_clan);
        ImageButton button_register_create_clan = findViewById(R.id.button_register_create_clan);

        button_register_join_clan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getFormUsername();
                String password = getFormPassword();
                String repeat_password = getFormRepeatPassword();
                if (isFormValid(username, password, repeat_password)) {
                    String password_sha = null;
                    try {
                        password_sha = calculateSHA1(password);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    assert password_sha != null;
                    // intent
                }
            }
        });

        button_register_create_clan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // intent
            }
        });
    }

    private String getFormUsername() {
        return input_register_username.getText().toString();
    }

    private String getFormPassword() {
        return input_register_password.getText().toString();
    }

    private String getFormRepeatPassword() {
        return input_register_repeat_password.getText().toString();
    }

    private boolean isFormValid(String username, String password, String repeat_password) {
        boolean isValid = true;

        if (getFormUsername().equals("")) {
            isValid = false;
        }

        if (getFormPassword().equals("") || !getFormPassword().equals(getFormRepeatPassword())) {
            isValid = false;
        }

        return isValid;
    }

    private static String calculateSHA1(String password) throws NoSuchAlgorithmException {
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(password.getBytes(StandardCharsets.UTF_8));

        return new BigInteger(1, crypt.digest()).toString(16);
    }

}