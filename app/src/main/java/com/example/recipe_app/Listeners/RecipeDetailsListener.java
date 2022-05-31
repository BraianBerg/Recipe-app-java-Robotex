package com.example.recipe_app.Listeners;

import com.example.recipe_app.Models.RecipeDetailsResponce;

public interface RecipeDetailsListener {
    void didFetch(RecipeDetailsResponce response, String message);
    void didError(String message);
}
