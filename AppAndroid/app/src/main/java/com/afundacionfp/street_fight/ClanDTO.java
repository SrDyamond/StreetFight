package com.afundacionfp.street_fight;

public class ClanDTO {

    private int id;
    private String name;
    private String urlIcon;
    private String acronym;
    private String color;

    public ClanDTO(int id, String name, String urlIcon, String acronym, String color) {
        this.id = id;
        this.name = name;
        this.urlIcon = urlIcon;
        this.acronym = acronym;
        this.color = color;
    }

    public ClanDTO(String urlIcon, String acronym, String color) {
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
