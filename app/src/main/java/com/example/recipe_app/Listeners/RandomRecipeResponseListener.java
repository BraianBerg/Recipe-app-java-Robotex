package com.example.recipe_app.Listeners;

import com.example.recipe_app.Models.RandomRecipeApiResponse;

public interface RandomRecipeResponseListener {

        void didFetch(RandomRecipeApiResponse response, String message);
        void didError(String message);
}
