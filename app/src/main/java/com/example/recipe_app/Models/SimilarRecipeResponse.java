package com.example.recipe_app.Models;

public class SimilarRecipeResponse {
        public int id;
        public String title;
        public String imageType;
        public int readyInMinutes;
        public int servings;
        public String sourceUrl;

        public SimilarRecipeResponse() {
                // needed for db
        }
}
