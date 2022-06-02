package com.example.recipe_app.Models;

public class Equipment {
    public String image;
    public String name;

    public Equipment() {
        // needed for db
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
