package com.afundacionfp.street_fight;

import android.content.Context;
import android.content.SharedPreferences;

// SUBSTITUIR POR UN SINGLETON SHARED PREFERENCES
public class UserPreferences {
    private Integer userId;
    private String username, sessionCookie;
    private Long expiration;
    private static UserPreferences userpreferences = null;

//    public UserPreferences(int userId, String username, String sessionCookie, long expiration) {
//        this.userId = userId;
//        this.username = username;
//        this.sessionCookie = sessionCookie;
//        this.expiration = expiration;
//    }
//
//    public UserPreferences(int userId, String username, String sessionCookie) {
//        this.userId = userId;
//        this.username = username;
//        this.sessionCookie = sessionCookie;
//    }

    public static UserPreferences getInstance() {
        if (userpreferences == null) {
            userpreferences = new UserPreferences();
        }
        return userpreferences;
    }

    public void setUserId(int userId, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE).edit();
        editor.putInt("user_id", userId);
        editor.apply();
        this.userId = userId;
    }

    public void setUsername(String username, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.apply();
        this.username = username;
    }

    public void setSessionCookie(String sessionCookie, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE).edit();
        editor.putString("session_cookie", sessionCookie);
        editor.apply();
        this.sessionCookie = sessionCookie;
    }

    public void setExpiration(long expiration, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE).edit();
        editor.putLong("expiration", expiration);
        editor.apply();
        this.expiration = expiration;
    }

    public int getUserId(Context context) {
        if (userId == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE);
            userId = sharedPreferences.getInt("user_id", 0);
        }
        return userId;
    }

    public String getUsername(Context context) {
        if (username == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE);
            username = sharedPreferences.getString("username", null);
        }
        return username;
    }

    public String getSessionCookie(Context context) {
        if (sessionCookie == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE);
            sessionCookie = sharedPreferences.getString("session_cookie", null);
        }
        return sessionCookie;
    }

    public long getExpiration(Context context) {
        if (expiration == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE);
            expiration = sharedPreferences.getLong("expiration", 0);
        }
        return expiration;
    }

    public void deleteUserid(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE).edit();
        editor.remove("user_id");
        editor.apply();
        userId = null;
    }

    public void deleteUsername(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE).edit();
        editor.remove("username");
        editor.apply();
        username = null;
    }

    public void deleteSessionCookie(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE).edit();
        editor.remove("session_cookie");
        editor.apply();
        sessionCookie = null;
    }

    public void deleteExpiration(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE).edit();
        editor.remove("expiration");
        editor.apply();
        expiration = null;
    }

    public void deleteAll(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE).edit();
        editor.remove("user_id");
        editor.remove("username");
        editor.remove("session_cookie");
        editor.remove("expiration");
        editor.apply();
        userId = null;
        username = null;
        sessionCookie = null;
        expiration = null;
    }

    public void savePreferences(Context context, int userId, String username, String sessionCookie){
        SharedPreferences.Editor editor = context.getSharedPreferences("Street_Fight_preferences", Context.MODE_PRIVATE).edit();
        editor.putInt("user_id", userId);
        editor.putString("username", username);
        editor.putString("session_cookie", sessionCookie);
        editor.apply();
        this.userId = userId;
        this.username = username;
        this.sessionCookie = sessionCookie;
    }
}
