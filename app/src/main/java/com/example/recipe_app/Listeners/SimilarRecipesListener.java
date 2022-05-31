package com.example.recipe_app.Listeners;

import com.example.recipe_app.Models.SimilarRecipeResponse;

import java.util.List;

public interface SimilarRecipesListener {
    void didFetch(List<SimilarRecipeResponse> responce, String message);
    void didError(String message);
}
