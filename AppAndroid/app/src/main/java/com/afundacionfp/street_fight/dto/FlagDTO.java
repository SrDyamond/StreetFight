package com.afundacionfp.street_fight.dto;

import androidx.annotation.Nullable;

public class FlagDTO {

    private int id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private boolean capturing;
    private ClanDTO clan;

    public FlagDTO(int id, String name, @Nullable String description, double latitude, double longitude, boolean capturing, @Nullable ClanDTO clan) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capturing = capturing;
        this.clan = clan;
    }

    public FlagDTO(int id, double latitude, double longitude, boolean capturing, @Nullable ClanDTO clan) {
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

    @Override
    public String toString() {
        return "FlagDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
