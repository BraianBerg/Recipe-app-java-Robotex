package com.example.recipe_app.Database.Callbacks;

import com.example.recipe_app.Models.RandomRecipeApiResponse;

public interface FirestoreCallback {
    void GotData(RandomRecipeApiResponse model, String messege);
    void DidNotGetData(String messege);
}
