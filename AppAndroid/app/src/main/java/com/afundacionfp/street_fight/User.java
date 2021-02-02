package com.afundacionfp.street_fight;

public class User {
    private int userId;
    private String username,sessionCookie;
    private long expiration;

    public User(int userId, String username, String sessionCookie, long expiration) {
        // TODO: ARREGLAR QUE PYTHON MANDA EL TIMESTAMP CON ESTE FORMATO 1.612871674002662E9
        this.userId = userId;
        this.username = username;
        this.sessionCookie = sessionCookie;
        this.expiration = expiration;
    }

    public User(int userId, String username, String sessionCookie) {
        this.userId = userId;
        this.username = username;
        this.sessionCookie = sessionCookie;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionCookie() {
        return sessionCookie;
    }

    public long getExpiration() {
        return expiration;
    }


}
