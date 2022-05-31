package com.example.recipe_app.Models;

import java.util.ArrayList;

public class Step {
        public int number;
        public String step;
        public ArrayList<Ingredient> ingredients;
        public ArrayList<Equipment> equipment;

        public Step() {
                // needed for db
        }
}
