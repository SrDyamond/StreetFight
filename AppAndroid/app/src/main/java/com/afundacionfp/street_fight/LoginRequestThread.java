package com.afundacionfp.street_fight;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// ESTE MÉTODO REALIZA UNA PETICIÓN CONTRA EL API DE FORMA MUY MANUAL, SUBSTITUIDO POR VOLLEY
public class LoginRequestThread extends Thread {

    private final String username;
    private String password_sha = "";

    public LoginRequestThread(String username, String password) {
        this.username = username;
        try {
            this.password_sha = hashPassword(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(password.getBytes(StandardCharsets.UTF_8));

        return new BigInteger(1, crypt.digest()).toString(16);
    }

    @Override
    public void run() {
        super.run();
        try {
            URL url = new URL("http://192.168.111.111:8000/user/" + username + "/session");
            Log.d("####URL", url.toString());
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            String requestBody = "{\"password_sha\": \"" + password_sha + "\"}";
            Log.d("####REQUEST BODY", requestBody);

            OutputStream os = con.getOutputStream();
            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(requestBodyBytes, 0, requestBodyBytes.length);

            InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(inputStreamReader);
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            Log.d("########RESPONSEEEEE", response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
