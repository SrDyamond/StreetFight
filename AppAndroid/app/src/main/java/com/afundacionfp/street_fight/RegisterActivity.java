package com.afundacionfp.street_fight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText inputRegisterUsername;
    EditText inputRegisterPassword;
    EditText inputRegisterRepeatPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.register_layout);

        inputRegisterUsername = findViewById(R.id.input_register_username);
        inputRegisterPassword = findViewById(R.id.input_register_password);
        inputRegisterRepeatPassword = findViewById(R.id.input_register_repeat_password);

        ImageButton button_register_join_clan = findViewById(R.id.button_register_join_clan);
        ImageButton button_register_create_clan = findViewById(R.id.button_register_create_clan);

        button_register_join_clan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validFormAndGoto(0);
            }
        });

        button_register_create_clan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validFormAndGoto(1);
            }
        });
    }

    private void validFormAndGoto(int option) {
        String username = getFormUsername();
        String password = getFormPassword();
        String repeat_password = getFormRepeatPassword();
        if (isFormValid(username, password, repeat_password)) {
            String passwordSha = null;
            try {
                passwordSha = calculateSHA1(password);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            assert passwordSha != null;
            // intent
            Intent intent = new Intent(getApplicationContext(), option == 0 ? SearchClanActivity.class : CreateClanActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("password_sha", passwordSha);
            intent.putExtra("from", "register");
            startActivity(intent);
        } else {
            Toast.makeText(RegisterActivity.this, "Rellene todos los campos correctamente", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFormUsername() {
        return inputRegisterUsername.getText().toString();
    }

    private String getFormPassword() {
        return inputRegisterPassword.getText().toString();
    }

    private String getFormRepeatPassword() {
        return inputRegisterRepeatPassword.getText().toString();
    }

    private boolean isFormValid(String username, String password, String repeat_password) {
        boolean isValid = true;

        if (username.equals("")) {
            isValid = false;
        }

        if (password.equals("") || !password.equals(repeat_password)) {
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