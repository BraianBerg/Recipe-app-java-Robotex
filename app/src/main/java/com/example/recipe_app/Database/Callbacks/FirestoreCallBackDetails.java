package com.example.recipe_app.Database.Callbacks;

import com.example.recipe_app.Models.RecipeDetailsResponce;

public interface FirestoreCallBackDetails {
    void GotDetails(RecipeDetailsResponce detailsResponse);
    void DidNotGetDetails(String message);
}
