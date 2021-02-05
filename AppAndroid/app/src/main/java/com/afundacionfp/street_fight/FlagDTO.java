package com.afundacionfp.street_fight;

public class FlagDTO {

    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private boolean capturing;
    private ClanDTO clan;

    public FlagDTO(int id, String name, double latitude, double longitude, boolean capturing, ClanDTO clan) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capturing = capturing;
        this.clan = clan;
    }

    public FlagDTO(int id, double latitude, double longitude, boolean capturing, ClanDTO clan) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capturing = capturing;
        this.clan = clan;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isCapturing() {
        return capturing;
    }

    public ClanDTO getClan() {
        return clan;
    }
}
