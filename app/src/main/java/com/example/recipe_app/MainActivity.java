package com.example.recipe_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.recipe_app.Adapters.RandomRecipeAdapter;
import com.example.recipe_app.Database.Callbacks.FirestoreCallback;
import com.example.recipe_app.Database.DbMethods;
import com.example.recipe_app.Listeners.RandomRecipeResponseListener;
import com.example.recipe_app.Listeners.RecipeClickListener;
import com.example.recipe_app.Models.RandomRecipeApiResponse;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DbMethods dbMethods = new DbMethods();
    ProgressDialog dialog;
    RequestManager manager;
    RandomRecipeAdapter randomRecipeAdapter;
    RecyclerView recyclerView;
    Spinner spinner;
    List<String> tags = new ArrayList<>();
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");

        searchView = findViewById(R.id.searchView_home);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tags.clear();
                tags.add(query);
                //manager.getRandomRecipes(randomRecipeResponseListener, tags);
                // vajab fixmimist
                dbMethods.GetResByDishType(firestoreCallbackfromDb, tags, getApplicationContext());
                dialog.show();
                Log.e("custom", "onQueryTextSubmit: ");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        spinner = findViewById(R.id.spinner_tags);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tags,
                R.layout.spinner_text
        );

        arrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(spinnerSelectedListener);

        manager = new RequestManager(this);
//       manager.getRandomRecipes(randomRecipeResponseListener);
//        dialog.show();

    }

    private final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiResponse response, String message) {
            Log.e("custom", "didFetch:" );
            dialog.dismiss();
            recyclerView = findViewById(R.id.recycler_random);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
            randomRecipeAdapter = new RandomRecipeAdapter(MainActivity.this, response.recipes, recipeClickListener);
            recyclerView.setAdapter(randomRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final FirestoreCallback firestoreCallbackfromDb = new FirestoreCallback() {
        @Override
        public void GotData(RandomRecipeApiResponse model, String messege) {
            PutDataToView(model);
        }

        @Override
        public void DidNotGetData(String messege) {
            Log.e(TAG, "DidNotGetData: " + messege);
        }
    };
    private void PutDataToView(RandomRecipeApiResponse response){
        Log.e("custom", "didFetch:" );
        dialog.dismiss();
        recyclerView = findViewById(R.id.recycler_random);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
        randomRecipeAdapter = new RandomRecipeAdapter(MainActivity.this, response.recipes, recipeClickListener);
        recyclerView.setAdapter(randomRecipeAdapter);
    }

    private final AdapterView.OnItemSelectedListener spinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.e("custom", "onItemSelected: õ" );
            tags.clear();
            tags.add(adapterView.getSelectedItem().toString());
            //manager.getRandomRecipes(randomRecipeResponseListener, tags);
            dbMethods.GetResByDishType(firestoreCallbackfromDb, tags, getApplicationContext());
            dialog.show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
//            Toast.makeText(MainActivity.this, id, Toast.LENGTH_SHORT).show();
       /*     startActivity(new Intent(MainActivity.this, RecipeDetailsActivity.class)
                    .putExtra("id", id).putExtra("boolSimilar", "false"));*/
        }
    };
}