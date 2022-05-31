package com.example.recipe_app.Models;

import java.util.ArrayList;

public class InstructionsResponse {
    public String name;
    public ArrayList<Step> steps;

    public InstructionsResponse() {
        // needed for db
    }
}
