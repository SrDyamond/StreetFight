package com.afundacionfp.street_fight;

import androidx.annotation.Nullable;

public class ClanDTO {

    private int id;
    private String name;
    private String urlIcon;
    private String acronym;
    private String color;

    public ClanDTO(int id, String name,@Nullable String urlIcon, @Nullable String acronym, String color) {
        this.id = id;
        this.name = name;
        this.urlIcon = urlIcon;
        this.acronym = acronym;
        this.color = color;
    }

    public ClanDTO(@Nullable String urlIcon,@Nullable String acronym, String color) {
        this.urlIcon = urlIcon;
        this.acronym = acronym;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrlIcon() {
        return urlIcon;
    }

    public String getAcronym() {
        return acronym;
    }

    public String getColor() {
        return color;
    }
}
