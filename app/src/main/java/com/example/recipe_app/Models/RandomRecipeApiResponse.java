package com.example.recipe_app.Models;

import java.util.ArrayList;

public class RandomRecipeApiResponse {
    public ArrayList<Recipe> recipes;

    public RandomRecipeApiResponse() {
        // needed for db
    }

    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }
}
