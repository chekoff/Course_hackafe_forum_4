package com.chekoff.hackafeforum;

/**
 * Created by Plamen on 25.03.2015.
 */
public class Categories {
    int id;
    String name;
    String text_color;
    String slug;

    public Categories() {
    }

    public Categories(int id, String name, String text_color, String slug) {
        this.id = id;
        this.name = name;
        this.text_color = text_color;
        this.slug = slug;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText_color() {
        return text_color;
    }

    public void setText_color(String text_color) {
        this.text_color = text_color;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
