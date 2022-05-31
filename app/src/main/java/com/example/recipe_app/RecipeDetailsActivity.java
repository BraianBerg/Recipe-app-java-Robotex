package com.example.recipe_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipe_app.Adapters.IngredientsAdapter;
import com.example.recipe_app.Adapters.InstructionsAdapter;
import com.example.recipe_app.Adapters.SimilarRecipeAdapter;
import com.example.recipe_app.Database.Callbacks.FirestoreAnalyzedInstructionsCallback;
import com.example.recipe_app.Database.Callbacks.FirestoreCallBackDetails;
import com.example.recipe_app.Database.DbMethodsDetails;
import com.example.recipe_app.Listeners.InstructionsListener;
import com.example.recipe_app.Listeners.RecipeClickListener;
import com.example.recipe_app.Listeners.RecipeDetailsListener;
import com.example.recipe_app.Listeners.SimilarRecipesListener;
import com.example.recipe_app.Models.InstructionsResponse;
import com.example.recipe_app.Models.RecipeDetailsResponce;
import com.example.recipe_app.Models.SimilarRecipeResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {
    private DbMethodsDetails dbMethodsDetails = new DbMethodsDetails();
    int id;
    TextView textView_meal_name, textView_meal_source, textView_meal_summary;
    ImageView imageView_meal_image;
    RecyclerView recycler_meal_ingredients, recycler_meal_similar, recycler_meal_instructions;
    RequestManager manager;
    ProgressDialog dialog;
    IngredientsAdapter ingredientsAdapter;
    SimilarRecipeAdapter similarRecipeAdapter;
    InstructionsAdapter instructionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details_activity);

        findViews();


        id= Integer.parseInt(getIntent().getStringExtra("id"));
        //panna detailsi kutsumisse kaasa et kontrollida et kas res on olemas dbs kui simislar res kutsustakse
        Boolean checkForResInDb =  Boolean.parseBoolean(getIntent().getStringExtra("boolSimilar"));
        manager = new RequestManager(this);
        dbMethodsDetails.GetRecipeDetailsFromDb(id, firestoreCallBackDetails, analyzedInstructionsCallback ,getApplicationContext(), checkForResInDb);

        //manager.getRecipeDetails(recipeDetailsListener, id);
        manager.getSimilarRecipes(similarRecipesListener, id);
        //manager.getInstructions(instructionsListener, id);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading details...");
        dialog.show();
    }

    private void findViews() {
        textView_meal_name = findViewById(R.id.textView_meal_name);
        textView_meal_source = findViewById(R.id.textView_meal_source);
        textView_meal_summary = findViewById(R.id.textView_meal_summary);
        imageView_meal_image = findViewById(R.id.imageView_meal_image);
        recycler_meal_ingredients = findViewById(R.id.recycler_meal_ingredients);
        recycler_meal_similar = findViewById(R.id.recycler_meal_similar);
        recycler_meal_instructions = findViewById(R.id.recycler_meal_instructions);

    }

    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponce response, String message) {
            RecipeDetailsSet(response);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private FirestoreAnalyzedInstructionsCallback analyzedInstructionsCallback = new FirestoreAnalyzedInstructionsCallback() {
        @Override
        public void GotAnalyzedInstructions(List<InstructionsResponse> response){
            GotInstructions(response);
        }

        @Override
        public void DidNotGetData(String message){

        }
    };
    private FirestoreCallBackDetails firestoreCallBackDetails = new FirestoreCallBackDetails() {
        @Override
        public void GotDetails(RecipeDetailsResponce detailsResponse) {
            RecipeDetailsSet(detailsResponse);
        }

        @Override
        public void DidNotGetDetails(String message) {
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    private void RecipeDetailsSet(RecipeDetailsResponce response) {
        dialog.dismiss();
        textView_meal_name.setText(response.title);
        textView_meal_source.setText(response.sourceName);
        textView_meal_summary.setText(response.summary);
        Picasso.get().load(response.image).into(imageView_meal_image);

        recycler_meal_ingredients.setHasFixedSize(true);
        recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        ingredientsAdapter = new IngredientsAdapter(RecipeDetailsActivity.this, response.extendedIngredients);
        recycler_meal_ingredients.setAdapter(ingredientsAdapter);
    }

    private final SimilarRecipesListener similarRecipesListener = new SimilarRecipesListener() {
        @Override
        public void didFetch(List<SimilarRecipeResponse> responce, String message) {
            recycler_meal_similar.setHasFixedSize(true);
            recycler_meal_similar.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            similarRecipeAdapter = new SimilarRecipeAdapter(RecipeDetailsActivity.this, responce, recipeClickListener);
            recycler_meal_similar.setAdapter(similarRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
        // Toast.makeText(RecipeDetailsActivity.this, id, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RecipeDetailsActivity.this, RecipeDetailsActivity.class)
        .putExtra("id", id).putExtra("boolSimilar", "true"));

        }
    };
    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didFetch(List<InstructionsResponse> response, String message) {
            GotInstructions(response);
        }

        @Override
        public void didError(String message) {

        }
    };

    private void GotInstructions(List<InstructionsResponse> response) {
        recycler_meal_instructions.setHasFixedSize(true);
        recycler_meal_instructions.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
        instructionsAdapter = new InstructionsAdapter(RecipeDetailsActivity.this, response);
        recycler_meal_instructions.setAdapter(instructionsAdapter);
    }

}




