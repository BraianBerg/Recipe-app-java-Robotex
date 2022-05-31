package com.example.recipe_app.Models;

import java.util.ArrayList;

public class ExtendedIngredient {
    public int id;
    public String aisle;
    public String image;
    public String consistency;
    public String name;
    public String nameClean;
    public String original;
    public String originalName;
    public double amount;
    public String unit;
    public ArrayList<String> meta;
    public Measures measures;
    public ArrayList<Equipment> equipment;

    public ExtendedIngredient() {
        // needed for db
    }
}
